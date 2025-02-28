import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Users, Search, HandHelping, ParkingSquare, Building2, UserCircle } from "lucide-react";

const Resident = () => {
    const navigate = useNavigate();
    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const menuRef = useRef(null); // Reference for the profile menu

    const handleLogout = () => {
        localStorage.clear();
        navigate("/JoinOrCreate");
    };

    // Close the profile menu if clicked outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsProfileMenuOpen(false);
            }
        };

        document.addEventListener('click', handleClickOutside);

        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Navigation Bar */}
            <header className="bg-white shadow-md py-4 w-full">
                <div className="max-w-7xl mx-auto px-4 flex items-center justify-between">
                    <div className="flex items-center space-x-4 w-full">
                        <button onClick={() => navigate('/')} className="hover:bg-gray-100 p-1 rounded-lg">
                            <Users className="h-7 w-7 text-[#4873AB]" />
                        </button>

                        {/* "Neighborly" is a clickable link to the homepage */}
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

                            {/* Profile Dropdown */}
                            <div className="relative" ref={menuRef}>
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

            {/* Main Content */}
            <main className="flex justify-center items-center min-h-screen bg-blue-50">
                <div className="text-center -mt-20">
                    <h2 className="text-4xl font-bold text-gray-800">Welcome, Resident!</h2>
                    <p className="text-gray-600 mt-4 text-lg">Engage with your neighborhood and participate in community activities.</p>
                </div>
            </main>
        </div>
    );
};

export default Resident;
