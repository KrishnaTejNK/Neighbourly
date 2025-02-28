import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {Eye, EyeOff, Users} from 'lucide-react';
import axios from 'axios';

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage("");

    try {
    const res = await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_LOGIN_ENDPOINT}`, { email, password });
      const { token, userType, neighbourhoodId } = res.data.data;

      // Store token and role in localStorage
      localStorage.setItem("token", token);
      localStorage.setItem("userType", userType);
      localStorage.setItem("neighbourhoodId", neighbourhoodId);
      setMessage("Login successful");

      // Redirect based on role
      if (userType === "COMMUNITY_MANAGER") {
        navigate("/communitymanager");
      } else if (userType === "RESIDENT") {
        navigate("/resident");
      } else if (userType === "USER") {
        navigate("/JoinOrCreate");
      } else if (userType === "ADMIN") {	
        navigate("/admin");
      }
      else {
        navigate("/");
      }
    } catch (error) {
      setMessage("Login failed");
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <div className="min-h-screen flex flex-col lg:flex-row">


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
              <h1 className="text-5xl font-bold text-white">Welcome Back!</h1>
              <p className="text-blue-100 mt-4 text-2xl">
                Sign in to access your account and continue your journey
              </p>
            </div>
          </div>
        </div>

        <div className="flex-1 flex items-center justify-center lg:w-1/2 bg-gray-50 border-gray-200 border-r-2">
          <div className="w-full max-w-md flex flex-col justify-center p-8 bg-white rounded-lg shadow-md border-2 border-gray-200">
            <div className="max-w-md w-full mx-auto space-y-8">
              <div>
                <h2 className="text-3xl font-bold text-gray-900">Sign in to your account</h2>
                <p className="mt-2 text-sm text-gray-600">
                  Or{' '}
                  <Link to="/register" className="font-medium text-[#4873AB] hover:text-[#4873AB]">
                    create a new account
                  </Link>
                </p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email address
                  </label>
                  <input
                      type="email"
                      required
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <input
                        id="password"
                        type={showPassword ? "text" : "password"}
                        required
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Enter your password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute inset-y-0 right-3 flex items-center text-gray-500">
                      {showPassword ? <Eye size={20} /> : <EyeOff size={20} />}
                    </button>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <input
                        id="remember-me"
                        type="checkbox"
                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                    />
                    <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-700">
                      Remember me
                    </label>
                  </div>

                  <Link
                      to="/forgotPassword"
                      className="text-sm font-medium text-[#4873AB] hover:text-[#1e40af]"
                  >
                    Forgot password?
                  </Link>
                </div>

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full bg-[#4873AB] text-white py-2 px-4 rounded-lg hover:bg-[#1e40af] focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isLoading ? 'Signing in...' : 'Sign in'}
                </button>

                {message && (
                    <div className={`p-4 rounded-lg ${
                        message.includes('failed') ? 'bg-red-50 text-red-500' : 'bg-green-50 text-green-500'
                    }`}>
                      {message}
                    </div>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
  );
};

export default Login;
