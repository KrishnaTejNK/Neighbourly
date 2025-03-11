import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const PostsFeed = () => {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
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

        fetchPosts();
    }, []);

    const handleReport = (postId) => {
        alert(`Reported post ID: ${postId}`);
        // Implement backend call if needed
    };

    // const viewProfile = (userId) => {
    //     navigate(`/details/${userId}`);
    // };

    const viewProfile = async (userId) => {
        try {
            const response = await axios.get(`http://172.17.2.103:8080/api/user/details/${userId}`);
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


    const createPost = () => {
        navigate('/createpost');
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
                    <div className="w-full max-w-lg">
                        {posts.map((post) => (
                            <div
                                key={post.postId}
                                className="bg-white shadow-md rounded-lg p-4 mb-4"
                            >
                                <div className="flex items-center justify-between mb-2">
                                    {/* Profile Section */}
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

                                {/* Post Content */}
                                <p className="text-lg font-bold">{post.postContent.split("\n")[0]}</p>
                                <p className="text-gray-700 mt-1">{post.postContent}</p>

                                {/* Actions */}
                                <div className="flex justify-end mt-2">
                                    <button
                                        onClick={() => handleReport(post.postId)}
                                        className="text-sm text-red-600 hover:underline"
                                    >
                                        Report
                                    </button>
                                </div>

                                {/* Timestamp */}
                                <p className="text-xs text-gray-500 mt-2">
                                    Posted on: {new Date(post.dateTime).toLocaleString()}
                                </p>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default PostsFeed;