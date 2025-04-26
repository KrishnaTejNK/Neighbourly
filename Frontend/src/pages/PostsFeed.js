import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const PostsFeed = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [userEmail] = useState(localStorage.getItem("email"));
  const [userid] = useState(localStorage.getItem("userid"));
  const [userRole, setUserRole] = useState("USER"); // Default role is Resident
  const [showPopup, setShowPopup] = useState({
    visible: false,
    message: "",
    type: "",
  });

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
          `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_POSTS}/${neighbourhoodId}`
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
          `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_ROLE}/${userEmail}`
        );
        setUserRole(response.data.role);
      } catch (error) {
        console.error("Error fetching user role:", error);
      }
    };

    fetchPosts();
    fetchUserRole();
  }, [userEmail]);

  const handleReport = async (postId) => {
    const neighbourhoodId = localStorage.getItem("neighbourhoodId");

    if (!neighbourhoodId || !userid) {

        setShowPopup({
          visible: true,
          message: "Unable to report post. Please log in again.",
          type: "",
        });
      
      return;
    }

    try {
      await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REPORT_REPORT}`, {
        neighbourhoodId: neighbourhoodId,
        postId: postId,
        reporterId: userid,
      });

        setShowPopup({
          visible: true,
          message:
            "Post reported successfully. A community manager will review it.",
          type: "success",
        });

    } catch (error) {
      console.error("Error reporting post:", error);
      
        setShowPopup({
          visible: true,
          message: "Failed to report post. Please try again later.",
          type: "",
        });

    }
    setTimeout(() => {
        setShowPopup({ visible: false, message: "", type: "" });
      }, 3000);
    
  };

  const handleDeletePost = async (postId) => {
    try {
      await axios.delete(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_POSTS_DELETE}/${postId}`);
      setPosts(posts.filter((post) => post.postId !== postId));
    } catch (error) {
      console.error("Error deleting post:", error);
    }
  };

  const viewProfile = async (userId) => {
    try {
      const response = await axios.get(
        `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_USER_DETAILS}/${userId}`
      );
      const user = response.data;
      navigate(`/profile/${user.email}`);
    } catch (error) {
      console.error("Error fetching User Details:", error);
    }
  };

  const createPost = () => {
    navigate("/createpost");
  };

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

  // Function to render a single post card
  const renderPostCard = (post, canDelete = false) => (
    <div
      key={post.postId}
      className="bg-white shadow-lg rounded-lg p-6 mb-6 hover:shadow-xl transition-shadow duration-300 border border-gray-100"
    >
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center">
          <div className="relative">
            <img
              src={`https://api.dicebear.com/7.x/identicon/svg?seed=${post.userId}`}
              alt="Profile"
              className="w-12 h-12 rounded-full mr-3 border-2 border-blue-400"
            />
            <div className="absolute bottom-0 right-2 w-3 h-3 bg-green-500 rounded-full border border-white"></div>
          </div>
          <div>
            <p className="font-semibold text-gray-800">{post.userName}</p>
            <p className="text-xs text-gray-500">{formatDate(post.dateTime)}</p>
          </div>
        </div>
        <button
          onClick={() => viewProfile(post.userId)}
          className="text-sm bg-blue-500 text-white px-4 py-1.5 rounded-full hover:bg-blue-600 transition-colors duration-300 flex items-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-4 w-4 mr-1"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fillRule="evenodd"
              d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
              clipRule="evenodd"
            />
          </svg>
          Profile
        </button>
      </div>

      <div className="mb-4 pb-4 border-b border-gray-100">
        <h3 className="text-xl font-bold text-gray-900 mb-2">
          {post.postContent.split("\n")[0]}
        </h3>
        <p className="text-gray-700 leading-relaxed">{post.postContent}</p>
      </div>

      <div className="flex justify-between items-center">
        <div className="flex space-x-4">
          <button
            onClick={() => handleReport(post.postId)}
            className="text-sm text-gray-500 hover:text-red-500 transition-colors duration-300 flex items-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-4 w-4 mr-1"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M3 6a3 3 0 013-3h10a1 1 0 01.8 1.6L14.25 8l2.55 3.4A1 1 0 0116 13H6a1 1 0 00-1 1v3a1 1 0 11-2 0V6z"
                clipRule="evenodd"
              />
            </svg>
            Report
          </button>
          {(canDelete || userRole === "Community Manager") && (
            <button
              onClick={() => handleDeletePost(post.postId)}
              className="text-sm text-gray-500 hover:text-red-500 transition-colors duration-300 flex items-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-4 w-4 mr-1"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                  clipRule="evenodd"
                />
              </svg>
              Delete
            </button>
          )}
        </div>
      </div>
    </div>
  );

  return (
    <div className="bg-gray-50 min-h-screen">
      <Navbar />
      {/* Popup Notification */}
      {showPopup.visible && (
        <div
          className={`fixed top-5 right-5 p-4 rounded-lg shadow-lg text-white ${
            showPopup.type === "success" ? "bg-green-500" : "bg-red-500"
          }`}
        >
          {showPopup.message}
        </div>
      )}
      <div className="container mx-auto px-4 py-10">
        <div className="max-w-6xl mx-auto">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold text-gray-800">Posts</h1>
            <button
              onClick={createPost}
              className="flex items-center bg-green-500 text-white px-6 py-3 rounded-full font-medium hover:bg-green-600 transition-colors duration-300 shadow-md"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-5 w-5 mr-2"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                  clipRule="evenodd"
                />
              </svg>
              Create New Post
            </button>
          </div>

          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
            </div>
          ) : posts.length === 0 ? (
            <div className="text-center py-16 bg-white rounded-lg shadow-md">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-16 w-16 mx-auto text-gray-400 mb-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"
                />
              </svg>
              <p className="text-xl text-gray-600">No posts available yet.</p>
              <p className="text-gray-500 mt-2">
                Be the first to share something with your community!
              </p>
            </div>
          ) : (
            <div className="flex flex-col md:flex-row gap-32">
              {/* Left Section - All Posts in Community */}
              <div className="w-full md:w-1/2">
                <div className="bg-white p-4 rounded-lg shadow-md mb-6">
                  <h2 className="text-2xl font-semibold text-gray-800 flex items-center">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-6 w-6 mr-2 text-blue-500"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z"
                      />
                    </svg>
                    All Community Posts
                  </h2>
                </div>
                {posts.map((post) => renderPostCard(post))}
              </div>

              {/* Right Section - Posts Created by Me or All Posts for Community Manager */}
              <div className="w-full md:w-1/2">
                <div className="bg-white p-4 rounded-lg shadow-md mb-6">
                  <h2 className="text-2xl font-semibold text-gray-800 flex items-center">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-6 w-6 mr-2 text-green-500"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                    {userRole === "COMMUNITY_MANAGER"
                      ? "All Posts"
                      : "My Posts"}
                  </h2>
                </div>
                {(userRole === "RESIDENT"
                  ? posts.filter((post) => post.userId.toString() === userid.toString())
                  : posts
                ).map((post) => renderPostCard(post, true))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PostsFeed;
