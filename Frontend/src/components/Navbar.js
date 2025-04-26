import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Users, Search, HandHelping, ParkingSquare, Building2, UserCircle, X } from "lucide-react";
import axios from "axios";

const Navbar = () => {
    const navigate = useNavigate();
    const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [postsCount, setPostsCount] = useState(0); 
    const currentEmail = localStorage.getItem("email");
    const userType = localStorage.getItem("userType");
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");

    const [actionMessage, setActionMessage] = useState("");

    useEffect(() => {
        if ((userType === "COMMUNITY_MANAGER" || userType === "ADMIN") && neighbourhoodId) {
            fetchNotifications(neighbourhoodId);
            fetchPostsCount(neighbourhoodId);
        }
    }, [userType, neighbourhoodId]);

    const fetchNotifications = async (neighbourhoodId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_NOTIFICATIONS_ENDPOINT}/${neighbourhoodId}`);
            setNotifications(response.data);
            setUnreadCount(response.data.length);
        } catch (error) {
            console.error("Error fetching notifications:", error);
        }
    };

    const fetchPostsCount = async (neighbourhoodId) => {
        try {
            const response = await axios.get(
                `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_POSTS}/${neighbourhoodId}`
            );
            setPostsCount(response.data.length);
        } catch (error) {
            console.error("Error fetching posts count:", error);
        }
    };

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    const handleProfile = () => {
        navigate(`/profile/${currentEmail}`);
    };

    const handleNotificationClick = () => {
        setIsNotificationsOpen(!isNotificationsOpen);
    };

    const handleViewProfile = async (userId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`);
            const user = response.data;
            const email = user.email;
            navigate(`/profile/${email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        }
    };

    const handleNotificationAction = async (id, action) => {
        try {
            const endpoint = action === 'approve'
                ? `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_JOIN_COMMUNITY_APPROVE_ENDPOINT}/${id}`
                : `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_JOIN_COMMUNITY_DENY_ENDPOINT}/${id}`;

            await axios.post(endpoint);
            setActionMessage(`${action.charAt(0).toUpperCase() + action.slice(1)} successfully`);
            setNotifications(notifications.filter(notification => notification.requestId !== id));
            setUnreadCount(prev => Math.max(0, prev - 1));
            setTimeout(() => setActionMessage(""), 3000);
        } catch (error) {
            console.error(`Error ${action} request:`, error);
        }
    };

    const handlePostsClick = () => {
        setPostsCount(0);
        navigate("/PostsFeed");
    };

    return (
        <header className="bg-white shadow-md py-4 w-full">
            <div className="max-w-7xl mx-auto px-4 flex items-center justify-between">
                <div className="flex items-center space-x-4 w-full">
                    <button onClick={() => navigate('/')} className="hover:bg-gray-100 p-1 rounded-lg">
                        <Users className="h-7 w-7 text-[#4873AB]" />
                    </button>

                    <h1 className="text-2xl font-bold text-[#4873AB] cursor-pointer" onClick={() => navigate('/')}>
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

                    <div className="flex items-center space-x-6">
                        <button
                            onClick={handlePostsClick}
                            className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2 relative"
                        >
                            <HandHelping className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Posts</span>
                            {postsCount > 0 && (
                                <div className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full min-w-[20px] h-5 flex items-center justify-center px-1">
                                    {postsCount}
                                </div>
                            )}
                        </button>

                        <button onClick={() => navigate("/ResidentParkingRentals")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                            <ParkingSquare className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Parking</span>
                        </button>

                        <button
                            onClick={() => navigate(userType === "COMMUNITY_MANAGER" ? "/CommunityManagerAmenities" : "/ResidentAmenities")}
                            className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2"
                        >
                            <Building2 className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Amenities</span>
                        </button>


                        {(userType === "COMMUNITY_MANAGER" || userType === "ADMIN") && (
                            <div className="relative">
                                <button
                                    onClick={handleNotificationClick}
                                    className="relative hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2 group"
                                >
                                    <div className="relative">
                                        <Bell className="w-6 h-6 text-[#4873AB]" />
                                        {unreadCount > 0 && (
                                            <div className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full min-w-[20px] h-5 flex items-center justify-center px-1">
                                                {unreadCount}
                                            </div>
                                        )}
                                    </div>
                                    <span className="text-sm font-medium text-gray-700">Notifications</span>
                                </button>

                                {isNotificationsOpen && (
                                    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm z-40" onClick={() => setIsNotificationsOpen(false)} />
                                )}

                                <div
                                    className={`fixed top-0 right-0 h-full w-80 bg-white shadow-lg transform transition-transform duration-300 ease-in-out z-50 ${
                                        isNotificationsOpen ? "translate-x-0" : "translate-x-full"
                                    }`}
                                >
                                    <div className="flex items-center justify-between p-4 border-b border-gray-200">
                                        <h3 className="text-lg font-semibold text-gray-800">Notifications</h3>
                                        <button
                                            onClick={() => setIsNotificationsOpen(false)}
                                            className="p-1 hover:bg-gray-100 rounded-full"
                                        >
                                            <X className="w-5 h-5 text-gray-500" />
                                        </button>
                                    </div>

                                    <div className="p-4 max-h-[calc(100vh-5rem)] overflow-y-auto">
                                        {Array.isArray(notifications) && notifications.length > 0 ? (
                                            <div className="space-y-3">
                                                {notifications.map((notification) => (
                                                    <div
                                                        key={notification.requestId}
                                                        className="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition-shadow"
                                                    >
                                                        <div className="space-y-3">
                                                            <div>
                                                                <button
                                                                    onClick={() => handleViewProfile(notification.user.id)}
                                                                >
                                                                    <p className="font-semibold text-gray-800">{notification.requestType}</p>
                                                                    <p className="text-sm text-gray-600 mt-1">
                                                                        {notification.user.name} wants to join the community
                                                                    </p>
                                                                </button>
                                                            </div>
                                                            <div className="flex space-x-2 pt-2">
                                                                <button
                                                                    onClick={() => handleNotificationAction(notification.requestId, "approve")}
                                                                    className="flex-1 bg-[#4873AB] text-white px-4 py-2 rounded-lg hover:bg-[#3b5d89] transition-colors"
                                                                >
                                                                    Approve
                                                                </button>
                                                                <button
                                                                    onClick={() => handleNotificationAction(notification.requestId, "deny")}
                                                                    className="flex-1 border border-red-500 text-red-500 px-4 py-2 rounded-lg hover:bg-red-50 transition-colors"
                                                                >
                                                                    Deny
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <div className="flex flex-col items-center justify-center py-8">
                                                <Bell className="w-12 h-12 text-gray-300 mb-2" />
                                                <p className="text-gray-500 text-lg font-medium">No Notifications</p>
                                                <p className="text-gray-400 text-sm text-center mt-1">
                                                    You'll see notifications here when you receive new requests
                                                </p>
                                            </div>
                                        )}

                                        {actionMessage && (
                                            <div className="fixed bottom-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg transform transition-transform duration-300">
                                                {actionMessage}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}

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
                                    <button onClick={handleProfile} className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                        Profile Info
                                    </button>
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
    );
};

export default Navbar;