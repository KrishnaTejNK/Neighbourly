import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { Users } from "lucide-react";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [emailMessage, setEmailMessage] = useState("");
  const [isEmailLoading, setIsEmailLoading] = useState(false);

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setIsEmailLoading(true);
    setEmailMessage("");

    try {
     const res = await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_FORGOT_PASSWORD_ENDPOINT}`,{ email });
      const resetToken = res.data.data.token;
      console.log(res)
      if (resetToken) {
        // Store the token in localStorage
        localStorage.setItem('resetToken', resetToken);

        setEmailMessage("Reset link sent to your email");
      } else {
        setEmailMessage("Failed to send the reset link.");
      }
    } catch (error) {
      setEmailMessage(error.response?.data?.message || "Failed to send reset link");
    } finally {
      setIsEmailLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex w-1/2 bg-[#4873AB] p-7 flex-col">
        <div className="flex items-center space-x-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-users h-8 w-8">
          </svg>
          <Link to="/" className="hover:bg-gray-400 p-1 rounded-lg">
            <Users className="h-7 w-7 text-white" />
          </Link>
          <Link to="/" className="hover:bg-gray-400 p-1 rounded-lg">
            <h1 className="text-2xl font-bold text-white whitespace-nowrap">
              Neighborly
            </h1>
          </Link>
        </div>

        <div className="flex flex-col justify-center items-center h-full p-8">
          <div className="mt-8 text-center">
            <h1 className="text-5xl font-bold text-white">Forgot Password?</h1>
            <p className="text-blue-100 mt-4 text-2xl">
              Don't worry, we'll help you reset it
            </p>
          </div>
        </div>
      </div>

      {/* Right Section */}
      <div className="flex-1 flex items-center justify-center lg:w-1/2 bg-gray-50 border-gray-200 border-r-2">
        <div className="w-full max-w-md flex flex-col justify-center p-8 bg-white rounded-lg shadow-md border-2 border-gray-200">
          <div className="max-w-md w-full mx-auto space-y-8">
            <h2 className="text-3xl font-bold text-gray-900">Forgot password</h2>

            {/* Email Section */}
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Enter Registered Email
                </label>
                <div className="flex gap-2">
                  <input
                    type="email"
                    required
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg"
                    placeholder="Enter your email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                  <button
                    onClick={handleEmailSubmit}
                    disabled={isEmailLoading}
                    className="bg-[#4873AB] text-white py-2 px-4 rounded-lg hover:bg-[#1e40af] disabled:opacity-50"
                  >
                    {isEmailLoading ? 'Sending...' : 'Reset'}
                  </button>
                </div>
                {emailMessage && (
                  <div className={`mt-2 p-2 rounded-lg text-sm ${
                    emailMessage.includes('Failed') ? 'bg-red-50 text-red-500' : 'bg-green-50 text-green-500'
                  }`}>
                    {emailMessage}
                  </div>
                )}
              </div>
            </div>

            <div className="text-center">
              <Link to="/login" className="mt-2 text-sm text-gray-600 font-medium text-[#4873AB] hover:text-[#1e40af]">
                Back to login
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
