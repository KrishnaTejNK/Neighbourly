import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Users, Search, HandHelping, ParkingSquare, Building2, UserCircle } from "lucide-react";
import axios from "axios";

const AdminPage = () => {
    const navigate = useNavigate();

    const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);  // Initialize notifications as an empty array
    const [loading, setLoading] = useState(true);
    const [actionMessage, setActionMessage] = useState(""); // New state for action messages

    const neighbourhoodId = localStorage.getItem("neighbourhoodId");

    // Fetch notifications when the component mounts or when the neighbourhoodId changes
    useEffect(() => {
        fetchNotifications(neighbourhoodId);
    }, [neighbourhoodId]);

    const fetchNotifications = async (neighbourhoodId) => {
        try {
            const response = await axios.get(`http://172.17.2.103:8080/api/help-requests/openCommunityRequests`);
            console.log(response.data.data);
            setNotifications(response.data.data);
        } catch (error) {
            console.error("Error fetching notifications:", error);
        } finally {
            setLoading(false);
        }
    };

    // Function to handle approve/deny actions for notifications
    const handleNotificationAction = async (id, action) => {
        try {
            const endpoint = action === 'approve'
                ? `http://172.17.2.103:8080/api/create-community/approve-create/${id}`
                : `http://172.17.2.103:8080/api/create-community/deny-create/${id}`;

            await axios.post(endpoint);
            // Show action message
            setActionMessage(`${action.charAt(0).toUpperCase() + action.slice(1)} successfully`);

            // Remove the notification from the list after action is performed
            setNotifications(notifications.filter(notification => notification.requestId !== id));

            // Hide the message after 3 seconds
            setTimeout(() => setActionMessage(""), 3000);
        } catch (error) {
            console.error(`Error ${action} request:`, error);
        }
    };

    // Function to handle logout action
    const handleLogout = () => {
        localStorage.clear(); // Clear user session
        navigate("/login"); // Redirect to login page
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navigation Bar */}
            <header className="bg-white shadow-md py-4 w-full">
                <div className="max-w-7xl mx-auto px-4 flex items-center justify-between">
                    <div className="flex items-center space-x-4 w-full">
                        <button onClick={() => navigate('/')} className="hover:bg-gray-100 p-1 rounded-lg">
                            <Users className="h-7 w-7 text-[#4873AB]" />
                        </button>

                        {/* Make "Neighbourly" a clickable link to homepage */}
                        <h1
                            className="text-2xl font-bold text-[#4873AB] cursor-pointer"
                            onClick={() => navigate('/')}
                        >
                            Neighborly
                        </h1>

                        <div className="relative w-full max-w-md">
                            <input
                                type="text"
                                placeholder="Search..."
                                className="w-full pl-4 pr-12 h-10 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#4873AB] focus:border-transparent"
                            />
                            <button className="absolute right-1 top-1/2 -translate-y-1/2 h-8 w-8 p-0 flex items-center justify-center bg-[#4873AB] text-white rounded-md hover:bg-blue-600 transition-colors">
                                <Search className="w-4 h-4" />
                            </button>
                        </div>

                        {/* Navigation Buttons */}
                        <div className="flex items-center space-x-6">
                            <button onClick={() => navigate("/help-requests")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                                <HandHelping className="w-6 h-6 text-[#4873AB]" />
                                <span className="text-sm font-medium text-gray-700">Help Requests</span>
                            </button>

                            <button onClick={() => navigate("/parking-rentals")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                                <ParkingSquare className="w-6 h-6 text-[#4873AB]" />
                                <span className="text-sm font-medium text-gray-700">Parking</span>
                            </button>

                            <button onClick={() => navigate("/public-bookings")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                                <Building2 className="w-6 h-6 text-[#4873AB]" />
                                <span className="text-sm font-medium text-gray-700">Public Places</span>
                            </button>

                            {/* Notifications Button with count */}
                            <button
                                onClick={() => setIsNotificationsOpen(!isNotificationsOpen)}
                                className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2"
                            >
                                <Bell className="w-6 h-6 text-[#4873AB]" />
                                <span className="text-sm font-medium text-gray-700">Notifications</span>
                            </button>

                            {/* Profile Icon with Dropdown */}
                            <div className="relative">
                                <button
                                    onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                                    className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2"
                                    title="Profile"
                                >
                                    <UserCircle className="w-7 h-7 text-[#4873AB]" />
                                </button>

                                {isProfileMenuOpen && (
                                    <div className="absolute right-0 mt-2 w-40 bg-white shadow-md rounded-lg py-2 z-50">
                                        <button
                                            onClick={handleLogout}
                                            className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
                                        >
                                            Logout
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            {/* Notifications Sidebar */}
            {isNotificationsOpen && (
                <div className="fixed inset-0 bg-black opacity-50 z-40" onClick={() => setIsNotificationsOpen(false)} />
            )}

            <div className={`fixed top-0 right-0 h-full w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out z-50 ${isNotificationsOpen ? "translate-x-0" : "translate-x-full"}`}>
                <div className="p-4 max-h-full overflow-y-auto">
                    <h3 className="text-lg font-semibold">Notifications</h3>
                    {loading ? (
                        <p>Loading...</p>
                    ) : Array.isArray(notifications) && notifications.length === 0 ? (
                        <p>No pending requests.</p>
                    ) : Array.isArray(notifications) ? (
                        notifications.map((notification) => (
                            <div key={notification.requestId} className="flex justify-between items-center p-2 hover:bg-gray-100 rounded-lg">
                                <div>
                                    <p className="font-semibold">{notification.status}</p>
                                    <p className="text-sm text-gray-600">{notification.description}</p>
                                </div>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => handleNotificationAction(notification.requestId, "approve")}
                                        className="text-green-600 hover:bg-green-100 px-3 py-1 rounded-lg"
                                    >
                                        Approve
                                    </button>
                                    <button
                                        onClick={() => handleNotificationAction(notification.requestId, "deny")}
                                        className="text-red-600 hover:bg-red-100 px-3 py-1 rounded-lg"
                                    >
                                        Deny
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>Invalid data format for notifications.</p>
                    )}
                    {/* Show Action Message */}
                    {actionMessage && (
                        <div className="mt-4 text-center text-sm text-green-600">
                            {actionMessage}
                        </div>
                    )}
                </div>
            </div>

            <main className="flex justify-center items-center min-h-screen bg-blue-50">
                <div className="text-center -mt-20">
                    <h2 className="text-4xl font-bold text-gray-800">Welcome, Admin!</h2>
                    <p className="text-gray-600 mt-4 text-lg">Manage your neighborhood effortlessly with ease and efficiency.</p>
                </div>
            </main>
        </div>
    );
};

export default AdminPage;
