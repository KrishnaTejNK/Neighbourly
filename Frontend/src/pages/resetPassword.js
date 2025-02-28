import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Eye, EyeOff, Users } from 'lucide-react';
import axios from 'axios';

const ResetPassword = () => {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setMessage("Passwords do not match");
      return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const email = urlParams.get('email');
    const token = localStorage.getItem('resetToken');

    if (!email || !token) {
      setMessage("Invalid or missing parameters");
      return;
    }
    

    setIsLoading(true);
    setMessage("");

    try {
      await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_PASSWORD_RESET_ENDPOINT}`, {
        email,
        password,
        token
      });

      setMessage("Password reset successful");
    } catch (error) {
      setMessage("Failed to reset password");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex w-1/2 bg-[#4873AB] p-7 flex-col">
        <div className="flex items-center space-x-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-users h-8 w-8"></svg>
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
            <h1 className="text-5xl font-bold text-white">Reset Password</h1>
            <p className="text-blue-100 mt-4 text-2xl">
              Create a new password for your account
            </p>
          </div>
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center lg:w-1/2 bg-gray-50 border-gray-200 border-r-2">
        <div className="w-full max-w-md flex flex-col justify-center p-8 bg-white rounded-lg shadow-md border-2 border-gray-200 dark:border-gray-200">
          <div className="max-w-md w-full mx-auto space-y-8">
            <div>
              <h2 className="text-3xl font-bold text-gray-900">Reset password</h2>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  New Password
                </label>
                <div className="relative">
                  <input
                    type={showPassword ? "text" : "password"}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Enter new password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-3 flex items-center text-gray-500"
                  >
                    {showPassword ? <Eye size={20} /> : <EyeOff size={20} />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Confirm New Password
                </label>
                <div className="relative">
                  <input
                    type={showConfirmPassword ? "text" : "password"}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Confirm new password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                  />
                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute inset-y-0 right-3 flex items-center text-gray-500"
                  >
                    {showConfirmPassword ? <Eye size={20} /> : <EyeOff size={20} />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full bg-[#4873AB] text-white py-2 px-4 rounded-lg hover:bg-[#1e40af] focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isLoading ? 'Resetting...' : 'Reset Password'}
              </button>

              {message && (
                <div className={`p-4 rounded-lg ${
                  message.includes('successful') ? 'bg-green-50 text-green-500' : 'bg-red-50 text-red-500'
                }`}>
                  {message}
                </div>
              )}

              <div className="text-center">
                <Link to="/login" className="text-sm font-medium text-[#4873AB] hover:text-[#1e40af]">
                  Back to login
                </Link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;
