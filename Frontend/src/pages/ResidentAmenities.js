import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";

const ResidentAmenities = () => {
  const [amenities, setAmenities] = useState([]);
  const [showBookingForm, setShowBookingForm] = useState(false);
  const [selectedAmenity, setSelectedAmenity] = useState(null);
  const [showPopup, setShowPopup] = useState({
    visible: false,
    message: "",
    type: "",
  });
  const [bookingDetails, setBookingDetails] = useState({
    name: "",
    description: "",
    bookingFrom: "",
    bookingTo: "",
    expectedAttendees: "",
  });

  // Set userId and neighbourhoodId at the beginning from localStorage
  const userId = localStorage.getItem("userid");
  const neighbourhoodId = localStorage.getItem("neighbourhoodId");

  // Memoizing fetchAmenities with useCallback
  const fetchAmenities = useCallback(async () => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_AMENITIES}/${neighbourhoodId}`
      );
      const groupedAmenities = groupAmenitiesByName(response.data);
      setAmenities(groupedAmenities);
    } catch (error) {
      console.error("Error fetching amenities:", error);
    }
  }, [neighbourhoodId]);  // Dependency on neighbourhoodId

  useEffect(() => {
    fetchAmenities();
  }, [fetchAmenities]);

  const groupAmenitiesByName = (amenities) => {
    const grouped = {};
    amenities.forEach((amenity) => {
      if (!grouped[amenity.name]) {
        grouped[amenity.name] = [];
      }
      grouped[amenity.name].push(amenity);
    });
    return grouped;
  };

  const handleBookClick = (amenity) => {
    setSelectedAmenity(amenity);
    setShowBookingForm(true);
  };

  const handleBookingInputChange = (e) => {
    setBookingDetails({ ...bookingDetails, [e.target.name]: e.target.value });
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

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_BOOKING_REQUESTS_CREATE}`, {
        ...bookingDetails,
        user_id: userId,
        neighbourhood_id: neighbourhoodId,
        amenityId: selectedAmenity.amenityId, // Include amenityId here
      });
      setShowPopup({
        visible: true,
        message: "Booking request submitted successfully!",
        type: "success",
      });

      setTimeout(() => {
        setShowPopup({ visible: false, message: "", type: "" });
      }, 5000);
      
      setShowBookingForm(false);
      setBookingDetails({
        name: "",
        description: "",
        bookingFrom: "",
        bookingTo: "",
        expectedAttendees: "",
      });
      fetchAmenities(); // Refresh the amenities list
    } catch (error) {
      console.error("Error submitting booking request:", error);
    }
  };


  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto p-6 max-w-6xl ">
        <div className="bg-white rounded-lg shadow-md p-8 mb-8">
          <h1 className="text-3xl font-bold mb-2 text-blue-800">
            Community Amenities
          </h1>
          <p className="text-gray-600 mb-6">
            Book facilities and spaces for your events
          </p>
          {/* Popup Notification */}
          {showPopup.visible && (
            <div
              className={`fixed top-5 right-5 p-4 rounded-lg shadow-lg text-white ${
                showPopup.type === "success" ? "bg-green-500" : "bg-red-500"
              }`}
            >
              {showPopup.message}
            </div>
          )}
          <div className="mb-8">
            {/* Display Amenities Grouped by Name */}
            {Object.keys(amenities).length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 bg-gray-100 rounded-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-16 w-16 text-gray-400 mb-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1.5}
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <p className="text-gray-500 text-lg">
                  No amenities are currently available.
                </p>
              </div>
            ) : (
              Object.keys(amenities).map((amenityName) => (
                <div key={amenityName} className="mb-6">
                  <div className="flex items-center mb-4">
                    <h2 className="text-2xl font-semibold text-gray-800">
                      {amenityName}
                    </h2>
                    <div className="ml-4 flex gap-2">
                      <span className="inline-flex items-center px-2 py-1 bg-green-100 text-green-600 text-xs font-medium rounded">
                        <span className="h-2 w-2 rounded-full bg-green-500 mr-1"></span>
                        Available
                      </span>
                      <span className="inline-flex items-center px-2 py-1 bg-red-100 text-red-600 text-xs font-medium rounded">
                        <span className="h-2 w-2 rounded-full bg-red-500 mr-1"></span>
                        Booked
                      </span>
                    </div>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {amenities[amenityName].map((amenity) => (
                      <div
                        key={amenity.amenityId}
                        className={`p-4 rounded-lg shadow transition-transform transform hover:scale-105 ${
                          amenity.status === "BOOKED"
                            ? "bg-gradient-to-r from-red-500 to-red-600 text-white"
                            : "bg-gradient-to-r from-green-500 to-green-600 text-white"
                        } ${
                          amenity.status === "AVAILABLE"
                            ? "cursor-pointer hover:shadow-lg"
                            : ""
                        }`}
                        onClick={() =>
                          amenity.status === "AVAILABLE" &&
                          handleBookClick(amenity)
                        }
                      >
                        <div className="flex flex-col">
                          <div className="flex justify-between items-center mb-2">
                            <span className="font-medium">Time Slot</span>
                            <span
                              className={`text-xs px-2 py-1 rounded-full ${
                                amenity.status === "BOOKED"
                                  ? "bg-red-700"
                                  : "bg-green-700"
                              }`}
                            >
                              {amenity.status}
                            </span>
                          </div>
                          <div className="text-sm opacity-90">
                            <div className="mb-1">
                              <span className="font-semibold">From:</span>{" "}
                              {formatDateTime(amenity.availableFrom)}
                            </div>
                            <div>
                              <span className="font-semibold">To:</span>{" "}
                              {formatDateTime(amenity.availableTo)}
                            </div>
                          </div>
                          {amenity.status === "AVAILABLE" && (
                            <button className="mt-3 bg-white bg-opacity-20 hover:bg-opacity-30 px-3 py-1 rounded text-sm font-medium">
                              Book Now
                            </button>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Booking Form Modal */}
      {showBookingForm && selectedAmenity && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 backdrop-blur-sm">
          <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-md">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-800">
                Book {selectedAmenity.name}
              </h2>
              <button
                onClick={() => setShowBookingForm(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6"
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
              </button>
            </div>
            <form onSubmit={handleBookingSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Event Name
                  </label>
                  <input
                    type="text"
                    name="name"
                    value={bookingDetails.name}
                    onChange={handleBookingInputChange}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Enter event name"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Event Description
                  </label>
                  <textarea
                    name="description"
                    value={bookingDetails.description}
                    onChange={handleBookingInputChange}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    rows="3"
                    placeholder="Describe your event"
                    required
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Start Time
                    </label>
                    <input
                      type="datetime-local"
                      name="bookingFrom"
                      value={bookingDetails.bookingFrom}
                      onChange={handleBookingInputChange}
                      className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      End Time
                    </label>
                    <input
                      type="datetime-local"
                      name="bookingTo"
                      value={bookingDetails.bookingTo}
                      onChange={handleBookingInputChange}
                      className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      required
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Expected Attendees
                  </label>
                  <input
                    type="number"
                    name="expectedAttendees"
                    value={bookingDetails.expectedAttendees}
                    onChange={handleBookingInputChange}
                    className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Number of people"
                    required
                  />
                </div>
                <div className="flex justify-end gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => setShowBookingForm(false)}
                    className="px-5 py-2.5 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-md font-medium"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-md font-medium"
                  >
                    Submit Booking
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResidentAmenities;
