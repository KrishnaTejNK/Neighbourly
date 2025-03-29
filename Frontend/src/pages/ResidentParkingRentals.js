import React, { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import { useNavigate } from "react-router-dom";

const ResidentParkingRentals = () => {
    const [parkingRentals, setParkingRentals] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [parkingRequests, setParkingRequests] = useState([]);
    const [newRental, setNewRental] = useState({
        spot: "",
        startTime: "",
        endTime: "",
        price: ""
    });
     const [confirmBooking, setConfirmBooking] = useState(null);
    const navigate = useNavigate();
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");
    const userId = localStorage.getItem("userid");
    const [showPopup, 
        setShowPopup] = useState({
        visible: false,
        message: "",
        type: "",
      });

    useEffect(() => {
        // Fetch all parking slots in the neighborhood
        axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING}/${neighbourhoodId}`)
            .then(response => setParkingRentals(response.data))
            .catch(error => console.error("Error fetching parking rentals:", error));

        // Fetch all parking requests for the logged-in user (if they own a parking slot)
        axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING_REQUESTS}/${userId}`)
            .then(response => setParkingRequests(response.data))
            .catch(error => console.error("Error fetching parking requests:", error));
    }, [neighbourhoodId, userId]);

    const getStatusColor = (status) => {
        switch (status) {
            case "AVAILABLE":
                return "bg-green-500";
            case "PENDING":
                return "bg-yellow-500";
            case "BOOKED":
                return "bg-red-500";
            default:
                return "bg-gray-500";
        }
    };

    const handleViewProfile = async (userId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`);
            const user = response.data;
            navigate(`/profile/${user.email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        }
    };

    const handleBookSlot = (rentalId, ownerId) => {
        setConfirmBooking({ rentalId, ownerId });
        setShowConfirmation(true);
    };

    const book = (confirm) => {
        if (!confirm) return;

        const requestData = {
            rentalId: confirmBooking.rentalId,
            userId: parseInt(userId),
            status: "PENDING"
        };
        
        axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING_REQUESTS}`, requestData)
            .then(() => {
                setShowPopup({
                    visible: true,
                    message: "Putting your request to the parking slot owner, please contact them for payment.",
                    type: "success",
                  });
                setTimeout(() => {
                    handleViewProfile(confirmBooking.ownerId);
                }, 3000);
            })
            .catch(error => console.error("Error creating booking request:", error));

        setShowConfirmation(false)
        setTimeout(() => {
            setShowPopup({ visible: false, message: "", type: "" });
          }, 9000);
    };

    const handleCreateRental = (event) => {
        event.preventDefault();

        const rentalData = {
            neighbourhoodId: parseInt(neighbourhoodId),
            userId: parseInt(userId),
            spot: newRental.spot,
            startTime: newRental.startTime,
            endTime: newRental.endTime,
            price: parseFloat(newRental.price)
        };

        axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING_CREATE}`, rentalData)
            .then(() => {
                setShowForm(false);
                window.location.reload();
            })
            .catch(error => console.error("Error creating rental:", error));
    };

    const handleApproveRequest = (requestId, rentalId) => {
        axios.put(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING_REQUESTS}/${requestId}/approve`)
            .then(() => {
                axios.put(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING}/${rentalId}/booked`);
                setShowPopup({
                    visible: true,
                    message: "Request approved. Parking slot is now booked.",
                    type: "success",
                  });
                window.location.reload();
            })
            .catch(error => console.error("Error approving request:", error));

            setTimeout(() => {
                setShowPopup({ visible: false, message: "", type: "" });
              }, 9000);
    };
    const redirectpage = () => {
        setShowConfirmation(false);
    }
    const handleDenyRequest = (requestId) => {
        axios.put(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PARKING_REQUESTS}/${requestId}/deny`)
            .then(() => {
                setShowPopup({
                    visible: true,
                    message: "Request denied.",
                    type: "",
                  });
                window.location.reload();
            })
            .catch(error => console.error("Error denying request:", error));

            setTimeout(() => {
                setShowPopup({ visible: false, message: "", type: "" });
              }, 9000);
    };

    return (
        <div className="bg-gray-50 min-h-screen ">
            <Navbar />
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
        <div className="max-w-6xl mx-auto">
            {/* Header Section */}
            <div className="flex justify-between items-center mb-8 mt-4">
                <h2 className="text-3xl font-bold text-gray-800">Parking Rentals</h2>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition duration-300 flex items-center gap-2 shadow-md"
                >
                    <span>Add Parking Slot</span>
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
                    </svg>
                </button>
            </div>

            {/* Form Section */}
            {showForm && (
            <div className="mb-8 bg-white rounded-xl shadow-lg p-6 border border-gray-200">
                    <h3 className="text-xl font-semibold mb-4 text-gray-800">Add New Parking Slot</h3>
                    <form
                        onSubmit={handleCreateRental}
                        className="flex flex-col gap-4"
                    >
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Spot Details</label>
                                <input
                                    type="text"
                                    placeholder="Enter spot Deatils"
                                    required
                                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                                    onChange={(e) => setNewRental({ ...newRental, spot: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Price ($)</label>
                                <input
                                    type="number"
                                    placeholder="Enter price"
                                    required
                                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                                    onChange={(e) => setNewRental({ ...newRental, price: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Start Time</label>
                                <input
                                    type="datetime-local"
                                    required
                                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                                    onChange={(e) => setNewRental({ ...newRental, startTime: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">End Time</label>
                                <input
                                    type="datetime-local"
                                    required
                                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                                    onChange={(e) => setNewRental({ ...newRental, endTime: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="flex justify-end gap-3 mt-2">
                            <button
                                type="button"
                                onClick={() => setShowForm(false)}
                                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 shadow-md transition"
                            >
                                Submit
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Available Parking Slots Section */}
            <div className="bg-white rounded-xl shadow-lg p-6 mb-8 border border-gray-200">
                <h3 className="text-xl font-semibold mb-6 text-gray-800">Available Parking Slots</h3>

                {parkingRentals.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {parkingRentals.map((rental) => (
                            <div
                                key={rental.rentalId}
                                className={`rounded-xl shadow-md overflow-hidden border ${getStatusColor(rental.status)} bg-opacity-10`}
                            >
                                <div className={`p-4 text-white ${getStatusColor(rental.status)}`}>
                                    <p className="text-xl font-bold">Spot details: {rental.spot}</p>
                                </div>
                                <div className={`p-4 ${getStatusColor(rental.status)} bg-opacity-5`}>
                                    <div className="flex justify-between items-center mb-3">
                                        <div className="text-sm text-gray-500">Start</div>
                                        <div className="font-medium">{new Date(rental.startTime).toLocaleString()}</div>
                                    </div>
                                    <div className="flex justify-between items-center mb-3">
                                        <div className="text-sm text-gray-500">End</div>
                                        <div className="font-medium">{new Date(rental.endTime).toLocaleString()}</div>
                                    </div>
                                    <div className="flex justify-between items-center mb-4">
                                        <div className="text-sm text-gray-500">Price</div>
                                        <div className="text-lg font-bold text-green-600">${rental.price}</div>
                                    </div>
                                    <button
                                        onClick={() => handleBookSlot(rental.rentalId, rental.userId)}
                                        className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 transition shadow-md flex items-center justify-center gap-2"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                        </svg>
                                        Book Now
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-8 text-gray-500">
                        No parking slots available at the moment.
                    </div>
                )}
            </div>

{/* Requests Section */}
<div className="bg-white rounded-xl shadow-lg p-6 border border-gray-200">
    <h3 className="text-xl font-semibold mb-6 text-gray-800">Requests for Your Parking Slots</h3>
    
    {parkingRequests.filter(request => request.status === "PENDING").length > 0 ? (
        <div className="space-y-4">
            {parkingRequests
                .filter(request => request.status === "PENDING")
                .map(request => (
                    <div key={request.requestId} className="p-5 bg-yellow-50 border border-yellow-200 rounded-lg shadow-sm">
                        <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                            <div className="space-y-1">
                                <div className="flex items-center gap-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-600" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
                                    </svg>
                                    <p className="font-medium">User: {request.name}</p>
                                </div>
                                <div className="flex items-center gap-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-gray-600" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
                                    </svg>
                                    <p>Spot: {request.spot}</p>
                                </div>
                                <div className="flex items-center gap-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-yellow-500" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2h-1V9a1 1 0 00-1-1z" clipRule="evenodd" />
                                    </svg>
                                    <p>Status: <span className="font-medium text-yellow-600">PENDING</span></p>
                                </div>
                            </div>
                            <div className="flex gap-3">
                                <button
                                    onClick={() => handleDenyRequest(request.requestId)}
                                    className="px-4 py-2 border border-red-300 text-red-600 rounded-lg hover:bg-red-50 transition flex items-center gap-2"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                                    </svg>
                                    Deny
                                </button>
                                <button
                                    onClick={() => handleApproveRequest(request.requestId, request.rentalId)}
                                    className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 shadow-sm transition flex items-center gap-2"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                    </svg>
                                    Approve
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
        </div>
    ) : (
        <div className="text-center py-8 text-gray-500 border border-gray-200 rounded-lg">
            No pending requests found.
        </div>
    )}
</div>
</div>
            
            {/* Confirmation Modal */}
            {showConfirmation && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
                    <div className="bg-white p-6 rounded-xl shadow-xl max-w-md w-full">
                        <h4 className="text-xl font-bold mb-4 text-gray-800">Confirm Booking</h4>
                        <p className="text-gray-600 mb-6">Are you sure you want to book this parking slot?</p>
                        <div className="flex justify-end gap-3">
                            <button 
                                onClick={() => redirectpage()}
                                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition"
                            >
                                Cancel
                            </button>
                            <button 
                                onClick={() => book(true)}
                                className="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 shadow-md transition"
                            >
                                Confirm Booking
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ResidentParkingRentals;
