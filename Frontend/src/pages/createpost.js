import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

const CreatePost = () => {
    const [title, setTitle] = useState("");
    const [category, setCategory] = useState("");
    const [otherCategory, setOtherCategory] = useState("");
    const [description, setDescription] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const email = localStorage.getItem("email");
        const neighbourhoodId = localStorage.getItem("neighbourhoodId");

        if (!email || !neighbourhoodId) {
            setMessage("Error: User not logged in.");
            return;
        }

        const postType = category === "Other" ? otherCategory : category;
        const postContent = `${title}\n${description}`; // Title as first line

        const postData = {
            email,
            neighbourhoodId: parseInt(neighbourhoodId),
            postType,
            postContent,
        };

        try {
            const response = await axios.post(
                `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_POSTS_CREATE}`,
                postData
            );
            setMessage(response.data);
            setTitle("");
            setCategory("");
            setOtherCategory("");
            setDescription("");
            
            // Redirect to PostFeed after successful post creation
            navigate("/PostSFeed");
        } catch (error) {
            setMessage("Error creating post.");
        }
    };

    return (
        <div>
            <Navbar />
            <div className="flex justify-center items-center min-h-screen bg-gray-100">
                <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-md">
                    <h2 className="text-2xl font-bold mb-4 text-center">Create Post</h2>

                    {message && (
                        <p className="text-center mb-4 text-sm text-green-600">{message}</p>
                    )}

                    <form onSubmit={handleSubmit}>
                        <label className="block mb-2 font-medium">Title</label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400 mb-4"
                        />

                        <label className="block mb-2 font-medium">Category</label>
                        <select
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                            required
                            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400 mb-4"
                        >
                            <option value="">Select a category</option>
                            <option value="Tools">Tools</option>
                            <option value="Emergency">Emergency</option>
                            <option value="Event Support">Event Support</option>
                            <option value="Other">Other</option>
                        </select>

                        {category === "Other" && (
                            <input
                                type="text"
                                value={otherCategory}
                                onChange={(e) => setOtherCategory(e.target.value)}
                                required
                                placeholder="Enter other category"
                                className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400 mb-4"
                            />
                        )}

                        <label className="block mb-2 font-medium">Description</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            required
                            rows="4"
                            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400 mb-4"
                        ></textarea>

                        <div className="flex justify-between">
                            <button
                                type="button"
                                onClick={() => navigate("/PostSFeed")}
                                className="w-1/2 bg-gray-500 text-white py-2 rounded-md hover:bg-gray-600 transition mr-2"
                            >
                                Back
                            </button>
                            <button
                                type="submit"
                                className="w-1/2 bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition"
                            >
                                Submit Post
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default CreatePost;
