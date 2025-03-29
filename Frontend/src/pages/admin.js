import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Users, Search, UserCircle } from "lucide-react";
import axios from "axios";

const AdminPage = () => {
    const navigate = useNavigate();

    const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);  // Initialize notifications as an empty array
    const [loading, setLoading] = useState(true);
    const [actionMessage, setActionMessage] = useState(""); // New state for action messages
    const [unreadCount, setUnreadCount] = useState(0);
    // const[user,setUser] = useState([]);
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");
    const currentemail = localStorage.getItem("email")
    const [neighbourhoods, setNeighbourhoods] = useState([]); // Store neighborhood data


    // Fetch notifications when the component mounts or when the neighbourhoodId changes
    useEffect(() => {
        fetchNotifications(neighbourhoodId);
    }, [neighbourhoodId]);

    const fetchNotifications = async (neighbourhoodId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_NOTIFICATIONS_OPEN_COMMUNITY_ENDPOINT}`);
            console.log(response.data.data);
            setNotifications(response.data.data);
            setUnreadCount(response.data.data.length);
            console.log("notification is :", response);
        } catch (error) {
            console.error("Error fetching notifications:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleNotificationAction = async (id, action) => {
        try {
            const endpoint = action === 'approve'
                ? `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_CREATE_COMMUNITY_APPROVE_ENDPOINT}/${id}`
                : `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_CREATE_COMMUNITY_DENY_ENDPOINT}/${id}`;

            await axios.post(endpoint);
            // Show action message
            setActionMessage(`${action.charAt(0).toUpperCase() + action.slice(1)} successfully`);

            // Remove the notification from the list after action is performed
            setNotifications(notifications.filter(notification => notification.requestId !== id));
            setUnreadCount(prev => Math.max(0, prev - 1));

            // Hide the message after 3 seconds
            setTimeout(() => setActionMessage(""), 3000);
        } catch (error) {
            console.error(`Error ${action} request:`, error);
        }
    };

    // Fetch neighborhoods when the page loads
    useEffect(() => {
        fetchNeighbourhoods();
    }, []);

    const fetchNeighbourhoods = async () => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_NEIGHBOURHOODS_GETALLNEIGHBOURHOODS}`);
            
            console.log("Neighbourhood data:", response.data);
            setNeighbourhoods(response.data);
        } catch (error) {
            console.error("Error fetching neighborhoods:", error);
        } finally {
            setLoading(false);
        }
    };

    // Function to handle logout action
    const handleLogout = () => {
        localStorage.clear(); // Clear user session
        navigate("/"); // Redirect to home page
    };

    const handleProfile = () => {
        navigate(`/profile/${currentemail}`);
    }

    const handleViewProfile = async (userId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`);
            
            console.log("The response is:", response);

            const user = response.data; // Access the user data from the response
            console.log("User is:", user);

            const email = user.email; // Extract the email from the user data
            console.log("Email is:", email);

            navigate(`/profile/${email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className=" min-h-screen bg-gray-50">
            {/* Navigation Bar - Keeping exactly as original */}
            <header className="flex items-center bg-white shadow-md py-4 w-full">
                <div className="max-w-7xl mx-auto px-4 flex items-center justify-between">
                    <div className="flex items-center space-x-4 w-full">
                        <button onClick={() => navigate('/')} className="hover:bg-gray-100 p-1 rounded-lg">
                            <Users className="h-7 w-7 text-[#4873AB]" />
                        </button>
                        <h1 className="text-2xl font-bold text-[#4873AB] cursor-pointer" onClick={() => navigate('/')}>Neighborly</h1>
                        <div className="relative w-full max-w-md">
                            <input type="text" placeholder="Search..." className="w-full pl-4 pr-12 h-10 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#4873AB] focus:border-transparent" />
                            <button className="absolute right-1 top-1/2 -translate-y-1/2 h-8 w-8 p-0 flex items-center justify-center bg-[#4873AB] text-white rounded-md hover:bg-blue-600 transition-colors">
                                <Search className="w-4 h-4" />
                            </button>
                        </div>
                        <div className="flex items-center space-x-6">

                            <button
                                onClick={() => setIsNotificationsOpen(!isNotificationsOpen)}
                                className="relative hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2 group"
                            >
                                <div className="relative">
                                    <Bell className="w-6 h-6 text-[#4873AB]" />
                                    {unreadCount > 0 && (
                                        <div className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full min-w-[20px] h-5 flex items-center justify-center px-1 transform transition-transform group-hover:scale-110">
                                            {unreadCount}
                                        </div>
                                    )}
                                </div>
                                <span className="text-sm font-medium text-gray-700">Notifications</span>
                            </button>


                            {/* Profile Icon with Dropdown */}
                            <div className="relative">
                                <button onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2" title="Profile">
                                    <UserCircle className="w-7 h-7 text-[#4873AB]" />
                                </button>

                                {isProfileMenuOpen && (
                                    <div className="absolute right-0 mt-2 w-40 bg-white shadow-md rounded-lg py-2 z-50">
                                        <button onClick={handleProfile} className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                            Profile Info
                                        </button>
                                        <button onClick={handleLogout} className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100">
                                            Logout
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            {/* Notifications Sidebar - Keeping exactly as original */}
            {isNotificationsOpen && (
                <div className="fixed inset-0 bg-black opacity-50 z-40" onClick={() => setIsNotificationsOpen(false)} />
            )}

            <div className={`fixed top-0 right-0 h-full w-400 bg-white shadow-lg transform transition-transform duration-300 ease-in-out z-50 ${isNotificationsOpen ? "translate-x-0" : "translate-x-full"}`}>
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
                                    <button
                                        onClick={() => handleViewProfile(notification.userId)}
                                    >
                                        {notification.description}
                                    </button>
                                </div>
                                <div className="flex space-x-">
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

            {/* Welcome Banner - This is the new section */}
            <div className="text-center py-8 bg-white shadow-sm mb-6 mt-6 mx-auto max-w-4xl rounded-lg">
                <h2 className="text-3xl font-bold text-[#4873AB] mb-2">Welcome, Admin</h2>
                <p className="text-gray-600">You have access to manage all neighborhoods and community requests</p>
            </div>

            {/* Active Neighbourhoods Section - Keeping mostly as original with slight styling improvements */}
            <main className="max-w-7xl mx-auto p-6">
                <h2 className="text-3xl font-bold text-gray-800 mb-6">Active Neighborhoods</h2>
                <div className="bg-white shadow-md rounded-lg p-4">
                    <h3 className="text-xl font-semibold text-gray-700 mb-4 flex items-center">
                        <Users className="h-6 w-6 text-[#4873AB] mr-2" />
                        Total Neighborhoods: {neighbourhoods.length}
                    </h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        {loading ? (
                            <p>Loading neighborhoods...</p>
                        ) : neighbourhoods.length === 0 ? (
                            <p>No active neighborhoods found.</p>
                        ) : (
                            neighbourhoods.map((neighbourhood) => (
                                <div key={neighbourhood.neighbourhood_id} className="bg-gray-100 p-4 rounded-lg shadow-sm border border-gray-200">
                                    <button
                                        onClick={() => handleViewProfile(neighbourhood.managerId)}
                                        className="w-full text-left"
                                    >
                                    <h4 className="text-lg font-bold text-[#4873AB]">{neighbourhood.name}</h4>
                                    <p className="text-gray-600 mt-2">Location: {neighbourhood.location}</p>
                                    <p className="text-gray-600">Members: {neighbourhood.memberCount}</p>
                                    <p className="text-gray-600">Community Manager: {neighbourhood.managerName || "Not Assigned"}</p>
                                    </button>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminPage;