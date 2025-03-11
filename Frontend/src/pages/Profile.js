import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Mail, Phone, MapPin, Home, User } from 'lucide-react';

const Profile = () => {
    const { email } = useParams(); // Get the email from the URL
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_PROFILE}/${email}`); // Ensure the correct backend URL
                setUser(response.data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchUserDetails();
    }, [email]);

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="text-center p-8 bg-white rounded-lg shadow-md">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
                    <p className="text-gray-600 font-medium">Loading profile information...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="text-center p-8 bg-white rounded-lg shadow-md max-w-md w-full">
                    <div className="bg-red-100 text-red-600 p-4 rounded-lg mb-4">
                        <p className="font-bold">Error</p>
                        <p>{error}</p>
                    </div>
                    <button 
                        onClick={() => window.history.back()} 
                        className="mt-4 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-200"
                    >
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="text-center p-8 bg-white rounded-lg shadow-md">
                    <p className="text-gray-600 font-medium">No user data available</p>
                    <button 
                        onClick={() => window.history.back()} 
                        className="mt-4 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition duration-200"
                    >
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center py-12 px-4">
            <div className="max-w-md w-full bg-white shadow-lg rounded-lg overflow-hidden">
                {/* Header Section */}
                <div className="bg-gradient-to-r from-blue-500 to-blue-600 px-6 py-4">
                    <div className="flex items-center justify-center mb-4">
                        <div className="w-24 h-24 bg-white text-blue-500 flex items-center justify-center rounded-full shadow-md">
                            <User size={48} />
                        </div>
                    </div>
                    <h1 className="text-2xl font-bold text-white text-center">{user.name}</h1>
                    <div className="flex justify-center mt-2">
                        <span className="inline-block px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-medium">
                            {user.userType}
                        </span>
                    </div>
                </div>
                
                {/* Content Section */}
                <div className="px-6 py-4">
                    <div className="space-y-4">
                        <div className="flex items-center">
                            <Mail className="w-5 h-5 text-blue-500 mr-3" />
                            <div>
                                <p className="text-sm text-gray-500">Email</p>
                                <p className="text-gray-800">{user.email}</p>
                            </div>
                            <div className="ml-auto">
                                {user.isEmailVerified ? (
                                    <div className="flex items-center text-red-500">
                                        <x size={16} className="mr-1" />
                                        <span className="text-xs"> Not Verified</span>
                                    </div>
                                ) : (
                                    <div className="flex items-center text-green-500">
                                        {/*<check size={16} className="mr-1" />*/}
                                        <span className="text-xs">Verified</span>
                                    </div>
                                )}
                            </div>
                        </div>
                        
                        <div className="flex items-center">
                            <Phone className="w-5 h-5 text-blue-500 mr-3" />
                            <div>
                                <p className="text-sm text-gray-500">Phone</p>
                                <p className="text-gray-800">{user.contact || 'Not provided'}</p>
                            </div>
                        </div>
                        
                        <div className="flex items-center">
                            <Home className="w-5 h-5 text-blue-500 mr-3" />
                            <div>
                                <p className="text-sm text-gray-500">Neighborhood ID</p>
                                <p className="text-gray-800">{user.neighbourhoodId || 'Not assigned'}</p>
                            </div>
                        </div>
                        
                        <div className="flex items-start">
                            <MapPin className="w-5 h-5 text-blue-500 mr-3 mt-0.5" />
                            <div>
                                <p className="text-sm text-gray-500">Address</p>
                                <p className="text-gray-800">{user.address || 'Not provided'}</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                {/* Footer Section */}
                <div className="border-t border-gray-200 px-6 py-4">
                    <button 
                        onClick={() => window.history.back()} 
                        className="w-full text-center py-2 text-blue-600 hover:text-blue-800 font-medium"
                    >
                        Return
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Profile;