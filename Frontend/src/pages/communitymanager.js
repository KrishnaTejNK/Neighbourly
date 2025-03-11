import React from "react";
import Navbar from "../components/Navbar";

const CommunityManager = () => {
    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />
            <main className="flex justify-center items-center min-h-screen bg-blue-50">
                <div className="text-center -mt-20">
                    <h2 className="text-4xl font-bold text-gray-800">Welcome, Community Manager!</h2>
                    <p className="text-gray-600 mt-4 text-lg">Manage your neighborhood effortlessly with ease and efficiency.</p>
                </div>
            </main>
        </div>
    );
};

export default CommunityManager;
