import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Users, Search, HandHelping, ParkingSquare, Building2, UserCircle } from "lucide-react";
import axios from "axios";

const Navbar = () => {
    const navigate = useNavigate();
    const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const currentEmail = localStorage.getItem("email");
    const userType = localStorage.getItem("userType");
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");

    useEffect(() => {
        if ((userType === "COMMUNITY_MANAGER" || userType === "ADMIN") && neighbourhoodId) {
            fetchNotifications(neighbourhoodId);
        }
    }, [userType, neighbourhoodId]);

    const fetchNotifications = async (neighbourhoodId) => {
        try {
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}/notifications/${neighbourhoodId}`);
            setNotifications(response.data);
            setUnreadCount(response.data.length);
        } catch (error) {
            console.error("Error fetching notifications:", error);
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
                        <button onClick={() => navigate("/PostsFeed")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                            <HandHelping className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Posts</span>
                        </button>

                        <button onClick={() => navigate("/parking-rentals")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                            <ParkingSquare className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Parking</span>
                        </button>

                        <button onClick={() => navigate("/public-bookings")} className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2">
                            <Building2 className="w-6 h-6 text-[#4873AB]" />
                            <span className="text-sm font-medium text-gray-700">Public Places</span>
                        </button>

                        {/* Show Notifications only for Community Managers and Admins */}
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

                                {/* Notifications Dropdown */}
                                {isNotificationsOpen && (
                                    <div className="absolute right-0 mt-2 w-80 bg-white shadow-lg rounded-lg py-2 z-50 max-h-96 overflow-y-auto">
                                        {notifications.length > 0 ? (
                                            notifications.map((notification) => (
                                                <div key={notification.requestId} className="px-4 py-3 border-b hover:bg-gray-100">
                                                    <p className="font-semibold text-gray-800">{notification.requestType}</p>
                                                    <p className="text-sm text-gray-600">{notification.user.name} wants to join the community</p>
                                                </div>
                                            ))
                                        ) : (
                                            <div className="p-4 text-center text-gray-500">No notifications</div>
                                        )}
                                    </div>
                                )}
                            </div>
                        )}

                        {/* Profile Dropdown */}
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
