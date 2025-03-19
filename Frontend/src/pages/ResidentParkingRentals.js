import React, { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import { useNavigate } from "react-router-dom";

const ResidentParkingRentals = () => {
    const [parkingRentals, setParkingRentals] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [bookingSlot, setBookingSlot] = useState(null);
    const [showConfirmation, setShowConfirmation] = useState(false);
    const [parkingRequests, setParkingRequests] = useState([]);
    const [newRental, setNewRental] = useState({
        spotNumber: "",
        startTime: "",
        endTime: "",
        price: ""
    });
     const [confirmBooking, setConfirmBooking] = useState(null);
    const navigate = useNavigate();
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");
    const userId = localStorage.getItem("userid");

    useEffect(() => {
        // Fetch all parking slots in the neighborhood
        axios.get(`http://172.17.2.103:8080/api/parking/${neighbourhoodId}`)
            .then(response => setParkingRentals(response.data))
            .catch(error => console.error("Error fetching parking rentals:", error));

        // Fetch all parking requests for the logged-in user (if they own a parking slot)
        axios.get(`http://172.17.2.103:8080/api/parking/requests/${userId}`)
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
            const response = await axios.get(`http://172.17.2.103:8080/api/user/details/${userId}`);
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

        axios.post("http://172.17.2.103:8080/api/parking/requests", requestData)
            .then(() => {
                alert("Putting your request to the parking slot owner, please contact them for payment.");
                setTimeout(() => {
                    handleViewProfile(confirmBooking.ownerId);
                }, 2000);
            })
            .catch(error => console.error("Error creating booking request:", error));

        setShowConfirmation(false)
    };

    const handleCreateRental = (event) => {
        event.preventDefault();

        const rentalData = {
            neighbourhoodId: parseInt(neighbourhoodId),
            userId: parseInt(userId),
            spotNumber: newRental.spotNumber,
            startTime: newRental.startTime,
            endTime: newRental.endTime,
            price: parseFloat(newRental.price)
        };

        axios.post("http://172.17.2.103:8080/api/parking/create", rentalData)
            .then(() => {
                setShowForm(false);
                window.location.reload();
            })
            .catch(error => console.error("Error creating rental:", error));
    };

    const handleApproveRequest = (requestId, rentalId) => {
        axios.put(`http://172.17.2.103:8080/api/parking/requests/${requestId}/approve`)
            .then(() => {
                axios.put(`http://172.17.2.103:8080/api/parking/${rentalId}/booked`);
                alert("Request approved. Parking slot is now booked.");
                window.location.reload();
            })
            .catch(error => console.error("Error approving request:", error));
    };
    const redirectpage = (event) => {
        setShowConfirmation(false);
    }
    const handleDenyRequest = (requestId) => {
        axios.put(`http://172.17.2.103:8080/api/parking/requests/${requestId}/deny`)
            .then(() => {
                alert("Request denied.");
                window.location.reload();
            })
            .catch(error => console.error("Error denying request:", error));
    };

    return (
        <div className="p-6">
            <Navbar />
            <h2 className="text-2xl font-bold mb-4">Parking Rentals</h2>

            <button
                onClick={() => setShowForm(true)}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition mb-4"
            >
                Add Parking Slot
            </button>

            {showForm && (
                <form
                    onSubmit={handleCreateRental}
                    className="flex flex-col gap-3 bg-gray-100 p-4 rounded-lg shadow-md"
                >
                    <input
                        type="text"
                        placeholder="Spot Number"
                        required
                        className="p-2 border border-gray-300 rounded"
                        onChange={(e) => setNewRental({ ...newRental, spotNumber: e.target.value })}
                    />
                    <input
                        type="datetime-local"
                        required
                        className="p-2 border border-gray-300 rounded"
                        onChange={(e) => setNewRental({ ...newRental, startTime: e.target.value })}
                    />
                    <input
                        type="datetime-local"
                        required
                        className="p-2 border border-gray-300 rounded"
                        onChange={(e) => setNewRental({ ...newRental, endTime: e.target.value })}
                    />
                    <input
                        type="number"
                        placeholder="Price"
                        required
                        className="p-2 border border-gray-300 rounded"
                        onChange={(e) => setNewRental({ ...newRental, price: e.target.value })}
                    />
                    <button
                        type="submit"
                        className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition"
                    >
                        Submit
                    </button>
                </form>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-6">
                {parkingRentals.map((rental) => (
                    <div
                        key={rental.rentalId}
                        className={`p-4 text-white rounded-lg shadow-md ${getStatusColor(rental.status)}`}
                    >
                        <p className="text-lg font-bold">Spot: {rental.spotNumber}</p>
                        <p>Start: {new Date(rental.startTime).toLocaleString()}</p>
                        <p>End: {new Date(rental.endTime).toLocaleString()}</p>
                        <p>Price: ${rental.price}</p>

                        <button onClick={() => handleBookSlot(rental.rentalId, rental.userId)}
                                className="bg-white text-black px-3 py-1 rounded mt-2">Book Now</button>
                    </div>
                ))}
            </div>

            <h2 className="text-2xl font-bold mt-8">Requests for Your Parking Slots</h2>
            <div className="mt-4">
                {parkingRequests.filter(request => request.status === "PENDING").length > 0 ? (
                    parkingRequests
                        .filter(request => request.status === "PENDING")
                        .map(request => (
                            <div key={request.requestId} className="p-4 bg-gray-100 shadow-md rounded-lg mb-4">
                                <p><strong>User:</strong> {request.userId}</p>
                                <p><strong>Spot Number:</strong> {request.rentalId}</p>
                                <p><strong>Status:</strong> {request.status}</p>
                                <div className="mt-2">
                                    <button
                                        onClick={() => handleApproveRequest(request.requestId, request.rentalId)}
                                        className="bg-green-600 text-white px-3 py-1 rounded mr-2"
                                    >
                                        Approve
                                    </button>
                                    <button
                                        onClick={() => handleDenyRequest(request.requestId)}
                                        className="bg-red-600 text-white px-3 py-1 rounded"
                                    >
                                        Deny
                                    </button>
                                </div>
                            </div>
                        ))
                ) : (
                    <p>No pending requests found.</p>
                )}
            </div>


            {showConfirmation && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-white p-6 rounded shadow-lg">
                        <p className="text-lg font-semibold">Do you want to book this slot?</p>
                        <button onClick={() => book(true)}>Yes</button>
                        <button onClick={() => redirectpage()}>No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ResidentParkingRentals;
