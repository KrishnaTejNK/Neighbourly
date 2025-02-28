import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import {UserPlus, Mail, Lock, Eye, EyeOff, Users} from 'lucide-react';

const Register = () => {

  const navigate = useNavigate();
  const [isError, setIsError] = useState(false);


  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [showOTPModal, setShowOTPModal] = useState(false);
  const [otpValues, setOtpValues] = useState({ otp1: ''});

  const validateForm = () => {
    const newErrors = {};

    if (!formData.fullName.trim()) {
      newErrors.fullName = 'Full name is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Invalid email address';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleOTPChange = (e) => {
    const { name, value } = e.target;
    setOtpValues(prev => ({
      ...prev,
      [name]: value
    }));
  };
  

  const handleOTPSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_VERIFY_OTP_ENDPOINT}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ otp: otpValues.otp1 }),
      });
  
      const data = await response.json();
  
      if (response.ok) {
        setMessage('OTP Verified.');
        setIsError(false);
        setShowOTPModal(false);
        setTimeout(() => navigate('/login'), 2000);
      } else {
        setMessage(data.message || 'OTP verification failed. Please try again.');
        setIsError(true);
      }
    } catch (error) {
      setMessage('Error occurred. Please try again.');
      setIsError(true);
    }
  };
  




  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REGISTER_ENDPOINT}`,
        {
        name: formData.fullName,
        email: formData.email,
        password: formData.password,
        confirmPassword: formData.password,
      });



      console.log('API response:', response.data);

      if (response.status === 201) {
        setMessage('Registration successful!');
        setShowOTPModal(true);
        setIsError(false); // Successful registration, so it's not an error
      } else {
        setMessage(`Error: ${response.data.message || 'Something went wrong'}`);
        setIsError(true); // Something went wrong, so it's an error
      }
    } catch (error) {
      setMessage('Error during registration');
      setIsError(true); // Error in API call
    } finally {
      setIsSubmitting(false);
    }

  };

  // const handleFileChange = (e) => {
  //   const file = e.target.files[0];
  //   if (file) {
  //     setFormData(prev => ({ ...prev, addressProof: file }));
  //   }
  // };

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



          <div className="flex flex-1 flex-col justify-center items-center text-center space-y-8">
            <h1 className="text-4xl font-bold text-white">Join our neighborhood community</h1>
            <p className="text-blue-100 text-lg">Connect with your neighbors, stay updated with local events, and build a stronger community together.</p>
          </div>
        </div>





        <div className="flex-1 flex items-center justify-center p-1 bg-gray-50">
          <div className="w-full max-w-md bg-white rounded-lg shadow-md border-2 border-gray-200 dark:border-gray-200">
            <div className="p-6">
              <div className="mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Create an Account</h2>
                <p className="text-gray-600 mt-2">Enter your details to register</p>
              </div>

              <p className="mb-5 text-sm text-gray-600">
                Or{' '}
                <Link to="/login" className="font-medium text-[#4873AB] hover:text-[#4873AB]">
                  login to account
                </Link>
              </p>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label htmlFor="fullName" className="block text-sm font-medium text-gray-700 mb-2">
                    Full Name
                  </label>
                  <div className="relative">
                    <UserPlus className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <input
                        id="fullName"
                        type="text"
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Name"
                        value={formData.fullName}
                        onChange={(e) => setFormData(prev => ({ ...prev, fullName: e.target.value }))}
                    />
                  </div>
                  {errors.fullName && (
                      <p className="mt-1 text-sm text-red-500">{errors.fullName}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                    Email
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <input
                        id="email"
                        type="email"
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="you@example.com"
                        value={formData.email}
                        onChange={(e) => setFormData(prev => ({ ...prev, email: e.target.value }))}
                    />
                  </div>
                  {errors.email && (
                      <p className="mt-1 text-sm text-red-500">{errors.email}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <input
                        id="password"
                        type={showPassword ? "text" : "password"}
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="••••••"
                        value={formData.password}
                        onChange={(e) => setFormData(prev => ({ ...prev, password: e.target.value }))}
                    />
                    <button
                        type="button"
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
                        onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? (<Eye className="h-4 w-4" />) : (<EyeOff className="h-4 w-4" />)}
                    </button>
                  </div>
                  {errors.password && (
                      <p className="mt-1 text-sm text-red-500">{errors.password}</p>
                  )}
                </div>

                <div>
                  <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
                    Confirm Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <input
                        id="confirmPassword"
                        type={showConfirmPassword ? "text" : "password"}
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="••••••"
                        value={formData.confirmPassword}
                        onChange={(e) => setFormData(prev => ({ ...prev, confirmPassword: e.target.value }))}
                    />
                    <button
                        type="button"
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    >
                      {showConfirmPassword ? (<Eye className="h-4 w-4" />) : (<EyeOff className="h-4 w-4" />)}
                    </button>
                  </div>
                  {errors.confirmPassword && (
                      <p className="mt-1 text-sm text-red-500">{errors.confirmPassword}</p>
                  )}
                </div>

                <div className="mb-4 text-center">
                  <button
                      type="submit"
                      className={`w-full py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-opacity-75 ${isSubmitting ? "opacity-50" : ""}`}
                      disabled={isSubmitting}
                  >
                    {isSubmitting ? 'Registering...' : 'Register'}
                  </button>
                </div>
              </form>

              <p className={`mt-3 text-center text-sm font-semibold ${isError ? 'text-red-500' : 'text-green-500'}`}>
                {message}
              </p>

              {showOTPModal && (
                  <div className="fixed inset-0 flex justify-center items-center bg-gray-600 bg-opacity-50">
                    <div className="bg-white p-8 rounded-lg">
                      <h2 className="text-lg font-bold mb-4">Verify OTP</h2>
                      <form onSubmit={handleOTPSubmit} className="space-y-4">
                        <input
                            type="text"
                            name="otp1"
                            placeholder="Enter OTP"
                            value={otpValues.otp1}
                            onChange={handleOTPChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-md"
                        />
                   
                        <div className="flex justify-center space-x-4">
                          <button
                              type="submit"
                              className="px-6 py-2 text-white bg-blue-500 rounded-md hover:bg-blue-600"
                          >
                            Submit
                          </button>
                          <button
                              type="button"
                              onClick={() => setShowOTPModal(false)}
                              className="px-6 py-2 text-gray-500 bg-gray-200 rounded-md hover:bg-gray-300"
                          >
                            Cancel
                          </button>
                        </div>
                      </form>
                    </div>
                  </div>
              )}
            </div>
          </div>
        </div>
      </div>
  );
}

export default Register;
