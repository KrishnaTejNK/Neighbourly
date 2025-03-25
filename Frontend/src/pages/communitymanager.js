import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import axios from "axios";

const CommunityManager = () => {
    const [residents, setResidents] = useState([]);
    const [reportedPosts, setReportedPosts] = useState([]); // New state for reported posts
    const navigate = useNavigate();

    useEffect(() => {
        const neighbourhoodId = localStorage.getItem("neighbourhoodId");

        // Fetch Residents
        const fetchResidents = async () => {
            try {
                const response = await axios.get(`http://localhost:8081/api/user/${neighbourhoodId}`);
                setResidents(response.data);
            } catch (error) {
                console.error("Error fetching residents:", error);
            }
        };

        // Fetch Reported Posts

        const fetchReportedPosts = async () => {
            try {
                const response = await axios.get(`http://localhost:8081/api/reports/${neighbourhoodId}`);

                // Extract each post and keep its associated reportId
                const allPosts = response.data.flatMap(report =>
                    report.posts.map(post => ({
                        ...post,         // Spread post data
                        reportId: report.id, // Attach the reportId
                    }))
                );

                setReportedPosts(allPosts); // Store only relevant data
                console.log(allPosts);
            } catch (error) {
                console.error("Error fetching reported posts:", error);
            }
        };



        fetchResidents();
        fetchReportedPosts();
    }, []);

    // Function to format the date in a more readable format
    const formatDate = (dateString) => {
        const options = {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        };
        return new Date(dateString).toLocaleDateString(undefined, options);
    };

    const viewProfile = async (userId) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/user/details/${userId}`);
            const user = response.data;
            navigate(`/profile/${user.email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        }
    };

    const handleApprovePost = async (reportId) => {
        try {
            await axios.put(`http://localhost:8081/api/reports/approve/${reportId}`);
            setReportedPosts(reportedPosts.filter((post) => post.reportId !== reportId));
        } catch (error) {
            console.error("Error approving post:", error);
        }
    };

    const handleDeletePost = async (reportId) => {
        try {
            await axios.delete(`http://localhost:8081/api/reports/delete/${reportId}`);
            setReportedPosts(reportedPosts.filter((post) => post.reportId !== reportId));
        } catch (error) {
            console.error("Error deleting post:", error);
        }
    };



    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />
            <main className="flex min-h-screen bg-blue-50 p-6">
                {/* Left Side: Residents List */}
                <div className="w-1/4 bg-white p-4 shadow-md rounded-md">
                    <h3 className="text-xl font-semibold mb-4">Residents</h3>
                    <ul>
                        {residents.length > 0 ? (
                            residents.map((resident) => (
                                <li
                                    key={resident.id}
                                    className="p-2 border-b hover:bg-gray-100 cursor-pointer"
                                    onClick={() => viewProfile(resident.id)}
                                >
                                    {resident.name} ({resident.email})
                                </li>
                            ))
                        ) : (
                            <p className="text-gray-500">No residents found.</p>
                        )}
                    </ul>
                </div>

                {/* Center: Welcome Message */}
                <div className="flex-1 flex justify-center items-center">
                    <div className="text-center -mt-20">
                        <h2 className="text-4xl font-bold text-gray-800">Welcome, Community Manager!</h2>
                        <p className="text-gray-600 mt-4 text-lg">
                            Manage your neighborhood effortlessly with ease and efficiency.
                        </p>
                    </div>
                </div>
                {/* Right Side: Reported Posts */}
                {/* Right Side: Reported Posts */}
                <div className="w-1/4 bg-white p-4 shadow-md rounded-md">
                    <h3 className="text-xl font-semibold mb-4">Reported Posts</h3>
                    {reportedPosts.length > 0 ? (
                        <ul>
                            {reportedPosts.map((post) => (
                                <li key={post.postId} className="p-2 border-b bg-gray-100 rounded-md shadow-sm mb-3">
                                    <div className="flex items-center justify-between mb-2">
                                        <div className="flex items-center">
                                            <img
                                                src={`https://api.dicebear.com/7.x/identicon/svg?seed=${post.userId}`}
                                                alt="Profile"
                                                className="w-10 h-10 rounded-full border-2 border-blue-400"
                                            />
                                            <div className="ml-3">
                                                <p className="font-semibold text-gray-800">{post.userId}</p>
                                                <p className="text-xs text-gray-500">{formatDate(post.dateTime)}</p>
                                            </div>
                                        </div>
                                        <button
                                            onClick={() => viewProfile(post.userId)}
                                            className="text-sm bg-blue-500 text-white px-3 py-1 rounded-full hover:bg-blue-600"
                                        >
                                            View Profile
                                        </button>
                                    </div>
                                    <div className="mb-2">
                                        <p className="font-medium text-gray-900">{post.postContent}</p>
                                    </div>
                                    <div className="flex gap-2">
                                        <button
                                            className="bg-green-500 text-white px-3 py-1 rounded"
                                            onClick={() => handleApprovePost(post.reportId)}
                                        >
                                            Approve
                                        </button>
                                        <button
                                            className="bg-red-500 text-white px-3 py-1 rounded"
                                            onClick={() => handleDeletePost(post.reportId)}
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-500">No reported posts.</p>
                    )}
                </div>


            </main>
        </div>
    );
};

export default CommunityManager;
