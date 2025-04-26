import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import axios from "axios";

const CommunityManager = () => {
    const [residents, setResidents] = useState([]);
    const [reportedPosts, setReportedPosts] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const neighbourhoodId = localStorage.getItem("neighbourhoodId");

        const fetchResidents = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER}/${neighbourhoodId}`);
                setResidents(response.data);
            } catch (error) {
                console.error("Error fetching residents:", error);
            }
        };

        const fetchReportedPosts = async () => {
            try {
                const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REPORT}/${neighbourhoodId}`);

                const allPosts = response.data.flatMap(report =>
                    report.posts.map(post => ({
                        ...post,
                        reportId: report.id,
                    }))
                );

                setReportedPosts(allPosts);
                console.log(allPosts);
            } catch (error) {
                console.error("Error fetching reported posts:", error);
            }
        };

        fetchResidents();
        fetchReportedPosts();
    }, []);

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
            const response = await axios.get(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`);
            const user = response.data;
            navigate(`/profile/${user.email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        }
    };

    const handleApprovePost = async (reportId) => {
        try {
            await axios.put(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REPORT_APPROVE}/${reportId}`);
            setReportedPosts(reportedPosts.filter((post) => post.reportId !== reportId));
        } catch (error) {
            console.error("Error approving post:", error);
        }
    };

    const handleDeletePost = async (reportId) => {
        try {
            await axios.delete(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REPORT_DELETE}/${reportId}`);
            setReportedPosts(reportedPosts.filter((post) => post.reportId !== reportId));
        } catch (error) {
            console.error("Error deleting post:", error);
        }
    };

    return (
        <div className="min-h-screen bg-blue-50">
            <Navbar />
            <main className="container mx-auto px-6 py-8">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Residents Column */}
                    <div className="bg-white rounded-xl shadow-md border border-blue-100">
                        <div className="bg-blue-100 p-4 border-b border-blue-100">
                            <h3 className="text-xl font-semibold text-blue-900">Residents</h3>
                        </div>
                        <div className="p-4 max-h-[600px] overflow-y-auto">
                            {residents.length > 0 ? (
                                <ul className="space-y-2">
                                    {residents.map((resident) => (
                                        <li
                                            key={resident.id}
                                            className="p-2 hover:bg-blue-50 rounded-lg cursor-pointer transition-colors"
                                            onClick={() => viewProfile(resident.id)}
                                        >
                                            <div className="flex justify-between items-center">
                                                <div>
                                                    <p className="font-medium text-gray-800">{resident.name}</p>
                                                    <p className="text-sm text-gray-500">{resident.email}</p>
                                                </div>
                                                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                                </svg>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="text-gray-500 text-center">No residents found.</p>
                            )}
                        </div>
                    </div>

                    {/* Welcome Column */}
                    <div className="flex items-center justify-center">
                        <div className="text-center bg-white rounded-xl shadow-md border border-blue-100 p-8">
                            <h2 className="text-3xl font-bold text-blue-800 mb-4">Welcome, Community Manager!</h2>
                            <p className="text-gray-600 mb-6">
                                Manage your neighborhood effortlessly with ease and efficiency.
                            </p>
                            <div className="inline-block p-4 bg-blue-50 rounded-xl">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.768-.231-1.49-.634-2.081M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.768.231-1.49.634-2.081m0 0a5.002 5.002 0 019.536 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                                </svg>
                            </div>
                        </div>
                    </div>

                    {/* Reported Posts Column */}
                    <div className="bg-white rounded-xl shadow-md border border-blue-100">
                        <div className="bg-blue-100 p-4 border-b border-blue-100">
                            <h3 className="text-xl font-semibold text-blue-900">Reported Posts</h3>
                        </div>
                        <div className="p-4 max-h-[600px] overflow-y-auto">
                            {reportedPosts.length > 0 ? (
                                <ul className="space-y-4">
                                    {reportedPosts.map((post) => (
                                        <li key={post.postId} className="bg-blue-50 rounded-lg p-4 border border-blue-100">
                                            <div className="flex items-center justify-between mb-3">
                                                <div className="flex items-center">
                                                    <img
                                                        src={`https://api.dicebear.com/7.x/identicon/svg?seed=${post.userId}`}
                                                        alt="Profile"
                                                        className="w-12 h-12 rounded-full border-2 border-blue-300"
                                                    />
                                                    <div className="ml-3">
                                                        <p className="font-semibold text-gray-800">{post.userId}</p>
                                                        <p className="text-xs text-gray-500">{formatDate(post.dateTime)}</p>
                                                    </div>
                                                </div>
                                                <button
                                                    onClick={() => viewProfile(post.userId)}
                                                    className="text-sm bg-blue-500 text-white px-3 py-1 rounded-full hover:bg-blue-600 transition-colors"
                                                >
                                                    View Profile
                                                </button>
                                            </div>
                                            <p className="text-gray-700 mb-3">{post.postContent}</p>
                                            <div className="flex space-x-2">
                                                <button
                                                    className="flex-1 bg-green-500 text-white py-2 rounded-lg hover:bg-green-600 transition-colors"
                                                    onClick={() => handleApprovePost(post.reportId)}
                                                >
                                                    Approve
                                                </button>
                                                <button
                                                    className="flex-1 bg-red-500 text-white py-2 rounded-lg hover:bg-red-600 transition-colors"
                                                    onClick={() => handleDeletePost(post.reportId)}
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="text-gray-500 text-center">No reported posts.</p>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default CommunityManager;