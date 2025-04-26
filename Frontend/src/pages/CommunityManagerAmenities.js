import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import { useNavigate } from "react-router-dom";

const CommunityManagerAmenities = () => {
  const [amenities, setAmenities] = useState([]);
  const [bookingRequests, setBookingRequests] = useState([]);
  const navigate = useNavigate();

  const [showPopup, setShowPopup] = useState({ visible: false, message: "", type: "" });
  const [newAmenity, setNewAmenity] = useState({
    name: "",
    availableFrom: "",
    availableTo: "",
  });
  const [showForm, setShowForm] = useState(false);

  const neighbourhoodId = localStorage.getItem("neighbourhoodId");

  const handleViewProfile = async (userId) => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`
      );
      const user = response.data;
      const email = user.email;
      navigate(`/profile/${email}`);
    } catch (error) {
      console.error("Error fetching User Details:", error);
    }
  };

  // Refactored fetchAmenities using useCallback
  const fetchAmenities = useCallback(async () => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_AMENITIES}`
      );
      setAmenities(response.data);
    } catch (error) {
      console.error("Error fetching amenities:", error);
    }
  }, []); // Only re-create when neighbourhoodId changes

  // Refactored fetchBookingRequests using useCallback
  const fetchBookingRequests = useCallback(async () => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_BOOKING_REQUESTS}/${neighbourhoodId}`
      );
      setBookingRequests(response.data);
    } catch (error) {
      console.error("Error fetching booking requests:", error);
    }
  }, [neighbourhoodId]); // Only re-create when neighbourhoodId changes

  useEffect(() => {
    fetchAmenities();
    fetchBookingRequests();
  }, [fetchAmenities, fetchBookingRequests]); // These functions are stable and won't change unless needed

  const handleApprove = async (bookingId, amenityId) => {
    try {
      await axios.put(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_BOOKING_REQUESTS_APPROVE}/${bookingId}`
      );
      setBookingRequests(
        bookingRequests.filter((request) => request.bookingId !== bookingId)
      );
      setAmenities(
        amenities.map((amenity) =>
          amenity.amenityId === amenityId
            ? { ...amenity, status: "BOOKED" }
            : amenity
        )
      );

      setShowPopup({ visible: true, message: "Booking Approved Successfully!", type: "success" });

      setTimeout(() => {
        setShowPopup({ visible: false, message: "", type: "" });
      }, 5000);
    } catch (error) {
      console.error("Error approving booking request:", error);
    }
  };

  const handleDeny = async (bookingId) => {
    try {
      await axios.put(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_BOOKING_REQUESTS_DENY}/${bookingId}`
      );
      setBookingRequests(
        bookingRequests.filter((request) => request.bookingId !== bookingId)
      );
      setShowPopup({ visible: true, message: "Booking Denied Successfully!", type: "error" });

      setTimeout(() => {
        setShowPopup({ visible: false, message: "", type: "" });
      }, 5000);
    } catch (error) {
      console.error("Error denying booking request:", error);
    }
  };

  const handleDelete = async (amenityId) => {
    try {
      await axios.delete(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_AMENITIES}/${amenityId}`);
      setAmenities(
        amenities.filter((amenity) => amenity.amenityId !== amenityId)
      );
    } catch (error) {
      console.error("Error deleting amenity:", error);
    }
  };

  const handleInputChange = (e) => {
    setNewAmenity({ ...newAmenity, [e.target.name]: e.target.value });
  };

  const handleCreateAmenity = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_AMENITIES}`, {
        ...newAmenity,
        neighbourhoodId,
      });

      setAmenities([...amenities, response.data]); // Update UI
      setNewAmenity({ name: "", availableFrom: "", availableTo: "" }); // Clear form
      setShowForm(false); // Hide form after submission
    } catch (error) {
      console.error("Error creating amenity:", error);
    }
  };

  const formatDateTime = (dateTime) => {
    return new Intl.DateTimeFormat("en-GB", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    }).format(new Date(dateTime));
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-6xl mx-auto p-6">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">
            <span>Amenities</span> Management
          </h1>
           {/* Popup Notification */}
           {showPopup.visible && (
            <div className={`fixed top-5 right-5 p-4 rounded-lg shadow-lg text-white ${showPopup.type === "success" ? "bg-green-500" : "bg-red-500"}`}>
                {showPopup.message}
            </div>
        )}
          <button
            onClick={() => setShowForm(!showForm)}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg transition-all shadow-md ${
              showForm
                ? "bg-gray-200 text-gray-700 hover:bg-gray-300"
                : "bg-blue-600 text-white hover:bg-blue-700"
            }`}
          >
            {showForm ? (
              <>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
                Cancel
              </>
            ) : (
              <>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 4v16m8-8H4"
                  />
                </svg>
                Add New Amenity
              </>
            )}
          </button>
        </div>

        {/* Create New Amenity Form */}
        {showForm && (
          <div className="bg-white rounded-xl shadow-lg mb-8 overflow-hidden border border-gray-100">
            <div className="bg-blue-50 px-6 py-4 border-b border-blue-100">
              <h2 className="text-xl font-semibold text-blue-700">
                Create New Amenity
              </h2>
            </div>
            <form
              onSubmit={handleCreateAmenity}
              className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6"
            >
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Amenity Name
                </label>
                <input
                  type="text"
                  name="name"
                  value={newAmenity.name}
                  onChange={handleInputChange}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  placeholder="e.g. Swimming Pool, Tennis Court, etc."
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Available From
                </label>
                <input
                  type="datetime-local"
                  name="availableFrom"
                  value={newAmenity.availableFrom}
                  onChange={handleInputChange}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Available To
                </label>
                <input
                  type="datetime-local"
                  name="availableTo"
                  value={newAmenity.availableTo}
                  onChange={handleInputChange}
                  className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  required
                />
              </div>
              <div className="md:col-span-2 flex justify-end">
                <button
                  type="submit"
                  className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-all shadow-md flex items-center gap-2"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 w-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                  Create Amenity
                </button>
              </div>
            </form>
          </div>
        )}
      <div className="flex gap-6">

        {/* List of Amenities */}
        <div className="w-1/2 bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
          <div className="bg-blue-50 px-6 py-4 border-b border-blue-100">
            <h2 className="text-xl font-semibold text-blue-700">
              Existing Amenities
            </h2>
          </div>

          <div className="p-6">
            {amenities.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-16 w-16 mx-auto text-gray-300 mb-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1}
                    d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"
                  />
                </svg>
                <p className="text-lg font-medium">No amenities found</p>
                <p className="mt-1">
                  Click the "Add New Amenity" button to create one
                </p>
              </div>
            ) : (
              <div className="grid gap-4">
                {amenities.map((amenity) => (
                  <div
                    key={amenity.amenityId}
                    className="flex flex-col md:flex-row justify-between items-start md:items-center p-4 bg-gray-50 rounded-lg border border-gray-100 hover:shadow-md transition-all"
                  >
                    <div className="mb-3 md:mb-0">
                      <h3 className="font-medium text-lg text-blue-700">
                        {amenity.name}
                      </h3>
                      <div className="flex items-center text-gray-600 mt-1">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-5 w-5 mr-2 text-gray-500"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                          />
                        </svg>
                        <span>
                          {formatDateTime(amenity.availableFrom)} -{" "}
                          {formatDateTime(amenity.availableTo)}
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => handleDelete(amenity.amenityId)}
                      className="bg-white text-red-600 border border-red-200 px-4 py-2 rounded-lg hover:bg-red-50 transition-all flex items-center gap-2"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                      Delete
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Section: Booking Requests */}
        <div className="w-1/2 bg-white rounded-xl shadow-lg border border-gray-100 p-6">
          <h2 className="text-xl font-semibold text-blue-700 mb-4">
            Booking Requests
          </h2>
          {bookingRequests.length === 0 ? (
            <p className="text-gray-500">No pending booking requests.</p>
          ) : (
            bookingRequests.map((request) => (
              <div
                key={request.booking_id}
                className="p-4 bg-gray-50 rounded-lg border mb-3"
              >
                <h3 className="font-medium text-lg">{request.name}</h3>
                <p className="text-gray-600">{request.description}</p>
                <p className="text-gray-600">
                  Requested By: {request.userName}
                </p>
                <div className="mt-3 flex gap-3">
                  <button
                    onClick={() => handleViewProfile(request.user_id)}
                    className="bg-gray-300 text-gray-700 px-3 py-2 rounded-lg hover:bg-gray-400"
                  >
                    View Profile
                  </button>
                  <button
                    onClick={() =>
                      handleApprove(request.bookingId, request.amenity_id)
                    }
                    className="bg-green-600 text-white px-3 py-2 rounded-lg hover:bg-green-700"
                  >
                    Approve
                  </button>
                  <button
                    onClick={() => handleDeny(request.bookingId)}
                    className="bg-red-600 text-white px-3 py-2 rounded-lg hover:bg-red-700"
                  >
                    Deny
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
        </div>
      </div>
    </div>
  );
};

export default CommunityManagerAmenities;
