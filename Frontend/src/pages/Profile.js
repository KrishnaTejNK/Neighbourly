import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Profile = () => {
    const { email } = useParams(); // Get the email from the URL
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:8081/api/user/profile/${email}`); // Ensure the correct backend URL
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
        return <div className="text-center text-gray-500">Loading...</div>;
    }

    if (error) {
        return <div className="text-center text-red-500">Error: {error}</div>;
    }

    if (!user) {
        return <div className="text-center text-gray-500">No user data available</div>;
    }

    return (
        <div className="max-w-md mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
            <div className="sm:flex sm:items-center px-6 py-4">
                <div className="text-center sm:text-left">
                    <h1 className="text-xl font-bold text-gray-900">{user.name}</h1>
                    <p className="text-sm text-gray-600">{user.email}</p>
                    <p className="text-sm text-gray-600">{user.contact || 'N/A'}</p>
                    <p className="text-sm text-gray-600">{user.neighbourhoodId || 'N/A'}</p>
                    <p className="text-sm text-gray-600">{user.address || 'N/A'}</p>
                    <p className="text-sm text-gray-600">{user.userType}</p>
                    <p className="text-sm text-gray-600">{user.isEmailVerified ? 'Email Verified' : 'Email Not Verified'}</p>
                </div>
            </div>
        </div>
    );
};

export default Profile;