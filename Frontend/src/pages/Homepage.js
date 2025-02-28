import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, Users, Search, UserPlus, LogIn, HandHelping, Building2, ParkingSquare, Heart, MapPin, Coffee, Sprout, MessageCircle, Handshake } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';
import hero1 from "../assets/img.png";
import hero2 from "../assets/img_5.png";
import hero3 from "../assets/img_3.png";

const Homepage = () => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const navigate = useNavigate();

  const headerIcons = [
    { icon: HandHelping, text: "Help Requests" },
    { icon: ParkingSquare, text: "Parking"},
    { icon: Building2, text: "Public Places" }

  ];

  const heroImages = [hero1, hero2, hero3];

  const communityFeatures = [
    {
      icon: Heart,
      title: "Strengthen Community Bonds",
      description: "Build lasting relationships with neighbors through shared activities and support."
    },
    {
      icon: MapPin,
      title: "Local Initiatives",
      description: "Discover and participate in neighborhood improvement projects and local events."
    },
    {
      icon: Coffee,
      title: "Meet & Greet",
      description: "Join community gatherings and make meaningful connections with those nearby."
    },
    {
      icon: Sprout,
      title: "Sustainable Living",
      description: "Share resources and promote eco-friendly practices within your community."
    }
  ];

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % heroImages.length);
    }, 5000);

    return () => clearInterval(timer);
  }, [heroImages.length]);

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % heroImages.length);
  };

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + heroImages.length) % heroImages.length);
  };

  return (
      <div className="min-h-screen bg-gray-50">
        <header className="bg-white shadow-md py-4 w-full">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4 w-full">
                <div className="flex items-center space-x-2">
                  <Link to="/" className="hover:bg-gray-100 p-1 rounded-lg">
                    <Users className="h-7 w-7 text-[#4873AB]" />
                  </Link>
                  <Link to="/" className="hover:bg-gray-100 p-1 rounded-lg">
                    <h1 className="text-2xl font-bold text-[#4873AB] whitespace-nowrap">
                      Neighborly
                    </h1>
                  </Link>
                </div>

                <div className="relative w-full max-w-md">
                  <input
                      type="text"
                      placeholder="Search..."
                      className="w-full pl-4 pr-12 h-10 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#4873AB] focus:border-transparent"
                  />
                  <button className="absolute right-1 top-1/2 -translate-y-1/2 h-8 w-8 p-0 flex items-center justify-center bg-[#4873AB] text-white rounded-md hover:bg-blue-600 transition-colors">
                    <Search className="w-4 h-4" />
                  </button>
                </div>

                <div className="flex items-center space-x-6">
                  {headerIcons.map((item, index) => (
                      <button
                          key={index}
                          onClick={() => navigate('/login')}
                          className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2"
                          title={item.text}
                      >
                        <item.icon className="w-6 h-6 text-[#4873AB]" />
                        <span className="text-sm font-medium text-gray-700">{item.text}</span>
                      </button>
                  ))}
                </div>
              </div>

              <div className="flex items-center space-x-2 ml-12">
                <button
                    onClick={() => navigate('/register')}
                    className="flex items-center space-x-1 h-10 px-3 rounded-lg border border-gray-300 hover:bg-gray-50 transition-colors text-sm"
                >
                  <UserPlus className="w-4 h-4" />
                  <span>Register</span>
                </button>
                <button
                    onClick={() => navigate('/login')}
                    className="flex items-center space-x-1 h-10 px-3 bg-[#4873AB] text-white rounded-lg hover:bg-blue-600 transition-colors text-sm"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Login</span>
                </button>
              </div>
            </div>
          </div>
        </header>

        <div className="relative h-[500px] overflow-hidden">
          <div
              className="absolute w-full h-full flex transition-transform duration-700 ease-in-out"
              style={{ transform: `translateX(-${currentSlide * 100}%)` }}
          >
            {heroImages.map((image, index) => (
                <div key={index} className="w-full h-full flex-shrink-0 relative">
                  <img src={image} alt={`Community ${index + 1}`} className="w-full h-full object-cover" />
                  <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
                    <div className="text-center text-white px-4">
                      <h1 className="text-5xl font-bold mb-6">Connect. Share. Thrive.</h1>
                      <p className="text-xl mb-8">Building stronger communities, one neighbor at a time</p>
                      <button
                          onClick={() => navigate('/register')}
                          className="bg-[#4873AB] text-white px-8 py-3 rounded-full text-lg font-semibold hover:bg-blue-600 transition-colors"
                      >
                        Join Your Community
                      </button>
                    </div>
                  </div>
                </div>
            ))}
          </div>

          <button onClick={prevSlide} className="absolute left-4 top-1/2 -translate-y-1/2 p-3 bg-white/80 rounded-full shadow-lg hover:bg-white transition-colors">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <button onClick={nextSlide} className="absolute right-4 top-1/2 -translate-y-1/2 p-3 bg-white/80 rounded-full shadow-lg hover:bg-white transition-colors">
            <ChevronRight className="w-6 h-6" />
          </button>
        </div>

        <div className="bg-gray-50 py-16">
          <div className="max-w-7xl mx-auto px-4">
            <h2 className="text-3xl font-bold text-center mb-12">Building Stronger Communities Together</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
              {communityFeatures.map((feature, index) => (
                  <div key={index} className="bg-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
                    <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mb-4">
                      <feature.icon className="w-6 h-6 text-[#4873AB]" />
                    </div>
                    <h3 className="text-xl font-semibold mb-3">{feature.title}</h3>
                    <p className="text-gray-600">{feature.description}</p>
                  </div>
              ))}
            </div>
          </div>
        </div>

        <div className="bg-white py-16">
          <div className="max-w-7xl mx-auto px-4">
            <h2 className="text-3xl font-bold text-center mb-12">Success Stories</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <div className="bg-gray-50 rounded-xl p-6 shadow transition-transform hover:scale-105">
                <MessageCircle className="w-8 h-8 text-[#4873AB] mb-4" />
                <p className="text-gray-600 mb-4">"Thanks to Neighborly, we organized a community garden that brings everyone together!"</p>
                <p className="font-semibold">- Sarah Johnson</p>
                <p className="text-sm text-gray-500">Oak Street Community</p>
              </div>
              <div className="bg-gray-50 rounded-xl p-6 shadow transition-transform hover:scale-105">
                <Handshake className="w-8 h-8 text-[#4873AB] mb-4" />
                <p className="text-gray-600 mb-4">"Found amazing neighbors who share tools and help each other out. It's like having an extended family!"</p>
                <p className="font-semibold">- Mike Chen</p>
                <p className="text-sm text-gray-500">Maple Heights</p>
              </div>
              <div className="bg-gray-50 rounded-xl p-6 shadow transition-transform hover:scale-105">
                <Heart className="w-8 h-8 text-[#4873AB] mb-4" />
                <p className="text-gray-600 mb-4">"Our neighborhood events have doubled in attendance since joining. The community spirit is amazing!"</p>
                <p className="font-semibold">- Emma Martinez</p>
                <p className="text-sm text-gray-500">River Valley</p>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-[#4873AB] text-white py-16">
          <div className="max-w-7xl mx-auto px-4 text-center">
            <h2 className="text-3xl font-bold mb-6">Ready to Join Your Community?</h2>
            <p className="text-xl mb-8">Connect with your neighbors and start making a difference today.</p>
            <div className="flex justify-center gap-4">
              <button
                  onClick={() => navigate('/register')}
                  className="bg-white text-[#4873AB] px-8 py-3 rounded-full text-lg font-semibold hover:bg-gray-100 transition-colors"
              >
                Get Started
              </button>
              <button
                  onClick={() => navigate('/login')}
                  className="border-2 border-white text-white px-8 py-3 rounded-full text-lg font-semibold hover:bg-white hover:text-[#4873AB] transition-colors"
              >
                Learn More
              </button>
            </div>
          </div>
        </div>
      </div>
  );
};

export default Homepage;