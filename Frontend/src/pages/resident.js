import React from "react";
import Navbar from "../components/Navbar"; // Import Navbar component

const Resident = () => {
    return (
        <div className="min-h-screen bg-gray-50">
            {/* Use the Navbar component */}
            <Navbar />

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
