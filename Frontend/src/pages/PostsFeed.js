import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const PostsFeed = () => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userEmail, setUserEmail] = useState(localStorage.getItem("email"));
    const [userid, setUserId] = useState(localStorage.getItem("userid"));
    const [userRole, setUserRole] = useState("USER"); // Default role is Resident

    const navigate = useNavigate();

    useEffect(() => {
        const fetchPosts = async () => {
            const neighbourhoodId = localStorage.getItem("neighbourhoodId");

            if (!neighbourhoodId) {
                console.error("User not logged in or neighborhood ID missing.");
                return;
            }

            try {
                const response = await axios.get(
                    `http://172.17.2.103:8080/api/posts/${neighbourhoodId}`
                );
                setPosts(response.data);
            } catch (error) {
                console.error("Error fetching posts:", error);
            } finally {
                setLoading(false);
            }
        };

        const fetchUserRole = async () => {
            try {
                const response = await axios.get(
                    `http://172.17.2.103:8080/api/user/role/${userEmail}`
                );
                setUserRole(response.data.role);
            } catch (error) {
                console.error("Error fetching user role:", error);
            }
        };

        fetchPosts();
        fetchUserRole();
    }, [userEmail]);

    const handleReport = (postId) => {
        alert(`Reported post ID: ${postId}`);
    };

    const handleDeletePost = async (postId) => {
        try {
            await axios.delete(`http://172.17.2.103:8080/api/posts/delete/${postId}`);
            setPosts(posts.filter((post) => post.postId !== postId));
        } catch (error) {
            console.error("Error deleting post:", error);
        }
    };

    const viewProfile = async (userId) => {
        try {
            const response = await axios.get(`http://172.17.2.103:8080/api/user/details/${userId}`);
            const user = response.data;
            navigate(`/profile/${user.email}`);
        } catch (error) {
            console.error("Error fetching User Details:", error);
        }
    };

    const createPost = () => {
        navigate("/createpost");
    };

    return (
        <div>
            <Navbar />

            <div className="flex flex-col items-center min-h-screen bg-gray-100 py-6">
                <h1 className="text-3xl font-bold mb-6">Community Posts</h1>

                <button
                    onClick={createPost}
                    className="mb-6 bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                >
                    Create New Post
                </button>

                {loading ? (
                    <p className="text-lg">Loading posts...</p>
                ) : posts.length === 0 ? (
                    <p className="text-lg">No posts available.</p>
                ) : (
                    <div className="flex w-full max-w-5xl">
                        {/* Left Section - All Posts in Community */}
                        <div className="w-1/2 pr-4">
                            <h2 className="text-2xl font-semibold mb-4">All Community Posts</h2>
                            {posts.map((post) => (
                                <div
                                    key={post.postId}
                                    className="bg-white shadow-md rounded-lg p-4 mb-4"
                                >
                                    <div className="flex items-center justify-between mb-2">
                                        <div className="flex items-center">
                                            <img
                                                src={`https://api.dicebear.com/7.x/identicon/svg?seed=${post.userId}`}
                                                alt="Profile"
                                                className="w-10 h-10 rounded-full mr-3"
                                            />
                                            <p className="font-semibold">{post.userName} {post.userId} </p>
                                        </div>
                                        <button
                                            onClick={() => viewProfile(post.userId)}
                                            className="text-sm bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                                        >
                                            View Profile
                                        </button>
                                    </div>
                                    <p className="text-lg font-bold">{post.postContent.split("\n")[0]}</p>
                                    <p className="text-gray-700 mt-1">{post.postContent}</p>

                                    <div className="flex justify-end mt-2">
                                        <button
                                            onClick={() => handleReport(post.postId)}
                                            className="text-sm text-red-600 hover:underline mr-2"
                                        >
                                            Report
                                        </button>
                                        {userRole === "Community Manager" && (
                                            <button
                                                onClick={() => handleDeletePost(post.postId)}
                                                className="text-sm text-red-600 hover:underline"
                                            >
                                                Delete
                                            </button>
                                        )}
                                    </div>

                                    <p className="text-xs text-gray-500 mt-2">
                                        Posted on: {new Date(post.dateTime).toLocaleString()}
                                    </p>
                                </div>
                            ))}
                        </div>

                        {/* Right Section - Posts Created by Me */}
                        {/* Right Section - Posts Created by Me (for RESIDENT) or All Posts (for COMMUNITY_MANAGER) */}
                        <div className="w-1/2 pl-4">
                            <h2 className="text-2xl font-semibold mb-4">
                                {userRole === "COMMUNITY_MANAGER" ? "All Posts" : "My Posts"}
                            </h2>

                            {(userRole === "RESIDENT" ? posts.filter((post) => post.userId == userid) : posts).map((post) => (

                                        <div
                                            key={post.postId}
                                            className="bg-white shadow-md rounded-lg p-4 mb-4"
                                        >
                                            <div className="flex items-center justify-between mb-2">
                                                <div className="flex items-center">
                                                    <img
                                                        src={`https://api.dicebear.com/7.x/identicon/svg?seed=${post.userId}`}
                                                        alt="Profile"
                                                        className="w-10 h-10 rounded-full mr-3"
                                                    />
                                                    <p className="font-semibold">{post.userName}</p>
                                                </div>
                                                <button
                                                    onClick={() => viewProfile(post.userId)}
                                                    className="text-sm bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                                                >
                                                    View Profile
                                                </button>
                                            </div>

                                            <p className="text-lg font-bold">{post.postContent.split("\n")[0]}</p>
                                            <p className="text-gray-700 mt-1">{post.postContent}</p>

                                            <div className="flex justify-end mt-2">
                                                <button
                                                    onClick={() => handleReport(post.postId)}
                                                    className="text-sm text-red-600 hover:underline mr-2"
                                                >
                                                    Report
                                                </button>
                                                <button
                                                    onClick={() => handleDeletePost(post.postId)}
                                                    className="text-sm text-red-600 hover:underline"
                                                >
                                                    Delete
                                                </button>
                                            </div>

                                            <p className="text-xs text-gray-500 mt-2">
                                                Posted on: {new Date(post.dateTime).toLocaleString()}
                                            </p>
                                        </div>
                                    )
                                )}


                        </div>

                    </div>
                )}
            </div>
        </div>
    );
};

export default PostsFeed;
