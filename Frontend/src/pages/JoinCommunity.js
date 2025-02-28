import React, { useState } from 'react';
import {User, Mail, Phone, Users} from 'lucide-react';
import axios from 'axios';
import {Link} from "react-router-dom";

const JoinCommunity = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        address1: '',
        pincode: ''
    });

    const [errors, setErrors] = useState({});
    const [message, setMessage] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const validateForm = () => {
        const newErrors = {};

        if (!formData.name.trim()) newErrors.name = 'Name is required';
        if (!formData.email.trim()) newErrors.email = 'Email is required';
        else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Invalid email address';
        if (!formData.phone.trim()) newErrors.phone = 'Phone number is required';
        if (!formData.address1.trim()) newErrors.address1 = 'Address Line 1 is required';
        if (!formData.pincode.trim()) newErrors.pincode = 'Pincode is required';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) return;

        setIsSubmitting(true);

        try {

          const response = await axios.post(`${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_JOIN_COMMUNITY_ENDPOINT}`, {
                name: formData.name,
                email: formData.email,
                phone: formData.phone,
                address: formData.address1,
                pincode: formData.pincode
            });

            if (response.status === 200) {
                setMessage('Waiting for a communnity manager to approve!');
                setFormData({ name: '', email: '', phone: '', address1: '', pincode: '' });
            } else {
                setMessage('Failed to join community. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
            setMessage('An error occurred. Please try again.');
        }

        setIsSubmitting(false);
    };
    return (
        <div className="min-h-screen flex">
            {/* Left side - Illustration Section */}





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
                    <h1 className="text-4xl font-bold text-white">Join an Existing Community</h1>
                    <p className="text-blue-100 text-lg">Connect with your Neighbours and grow together!!!</p>
                </div>
            </div>

            {/* Right side - Form Section */}
            <div className="flex-1 flex items-center justify-center p-1 bg-gray-50">
                <div className="w-full max-w-md bg-white rounded-lg shadow-md border-2 border-gray-200">
                    <div className="p-6">
                        <div className="mb-6">
                            <h2 className="text-2xl font-bold text-gray-900">Join your Community</h2>
                            <p className="text-gray-600 mt-2">Fill in your details to join</p>
                        </div>

                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Name</label>
                                <div className="relative">
                                    <User className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                                    <input
                                        type="text"
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg"
                                        placeholder="Your Name"
                                        value={formData.name}
                                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    />
                                </div>
                                {errors.name && <p className="mt-1 text-sm text-red-500">{errors.name}</p>}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                                <div className="relative">
                                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                                    <input
                                        type="email"
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg"
                                        placeholder="you@example.com"
                                        value={formData.email}
                                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    />
                                </div>
                                {errors.email && <p className="mt-1 text-sm text-red-500">{errors.email}</p>}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Phone Number</label>
                                <div className="relative">
                                    <Phone className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                                    <input
                                        type="text"
                                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg"
                                        placeholder="Your Phone Number"
                                        value={formData.phone}
                                        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                    />
                                </div>
                                {errors.phone && <p className="mt-1 text-sm text-red-500">{errors.phone}</p>}
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Address </label>
                                <input
                                    type="text"
                                    className="w-full p-2 border border-gray-300 rounded-lg"
                                    placeholder="Address Line"
                                    value={formData.address1}
                                    onChange={(e) => setFormData({ ...formData, address1: e.target.value })}
                                />
                                {errors.address1 && <p className="mt-1 text-sm text-red-500">{errors.address1}</p>}
                            </div>



                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Pincode</label>
                                <input
                                    type="text"
                                    className="w-full p-2 border border-gray-300 rounded-lg"
                                    placeholder="Pincode"
                                    value={formData.pincode}
                                    onChange={(e) => setFormData({ ...formData, pincode: e.target.value })}
                                />
                            </div>

                            <button type="submit" className="w-full bg-[#4873AB] text-white py-2 px-4 rounded-lg hover:bg-[#1e40af]">
                                {isSubmitting ? 'Submitting...' : 'Join Now'}
                            </button>
                            {message && <div className="p-4 rounded-lg bg-green-50 text-green-500">{message}</div>}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default JoinCommunity;