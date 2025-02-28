import React, { useState, useEffect, useRef } from 'react';
import { Users, Search, HandHelping, Building2, ParkingSquare, Heart, MapPin, Coffee, Sprout, MessageCircle, Handshake, UserCircle } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

const JoinOrCreate = () => {
  const navigate = useNavigate();
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  const menuRef = useRef(null); // Create a ref for the profile menu

  const headerIcons = [
    { icon: HandHelping, text: "Help Requests" },
    { icon: ParkingSquare, text: "Parking" },
    { icon: Building2, text: "Public Places" }
  ];

  const handleCreateCommunity = () => {
    navigate('/CreateCommunity');
  };

  const handleJoinCommunity = () => {
    navigate('/joinCommunity');
  };

  const handleLogout = () => {
    navigate('/');
  };

  const scrollToHero = () => {
    const heroSection = document.getElementById('hero-section');
    if (heroSection) {
      heroSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

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

  // Close the profile menu when clicking outside of it
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsProfileMenuOpen(false);
      }
    };

    document.addEventListener('click', handleClickOutside);

    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  return (
      <div className="min-h-screen bg-gray-50">
        {/* Header */}
        <header className="bg-white shadow-md py-4 w-full">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4 w-full">
                <div className="flex items-center space-x-2">
                  <Link to="/" className="hover:bg-gray-100 p-1 rounded-lg">
                    <Users className="h-7 w-7 text-[#4873AB]" />
                  </Link>
                  <Link to="/" className="hover:bg-gray-100 p-1 rounded-lg">
                    <h1 className="text-2xl font-bold text-[#4873AB] whitespace-nowrap">Neighborly</h1>
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

                {/* Profile Menu Section */}
                <div className="relative ml-4" ref={menuRef}>
                  <button
                      onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                      className="hover:bg-gray-100 p-2 rounded-lg flex items-center space-x-2"
                      title="Profile"
                  >
                    <UserCircle className="w-7 h-7 text-[#4873AB]" />
                  </button>

                  {isProfileMenuOpen && (
                      <div className="absolute right-0 mt-2 w-40 bg-white shadow-md rounded-lg py-2 z-50">
                        <button
                            onClick={handleLogout}
                            className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100"
                        >
                          Logout
                        </button>
                      </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Hero Section */}
        <div id="hero-section" className="relative h-[500px] flex items-center justify-center bg-[#4873AB] overflow-hidden">
          <div className="absolute inset-0 opacity-50" style={{
            backgroundImage: "url('https://source.unsplash.com/1600x900/?community,people')",
            backgroundSize: 'cover',
            backgroundPosition: 'center'
          }}></div>

          <div className="relative z-10 w-full max-w-4xl text-center text-white">
            <h2 className="text-5xl font-extrabold mb-6">Connect. Share. Grow.</h2>
            <p className="text-xl mb-8">Choose your next step to connect with your neighborhood community.</p>

            <div className="flex flex-col md:flex-row justify-center gap-6">
              <button
                  onClick={handleCreateCommunity}
                  className="py-3 px-6 bg-white text-[#4b6cb7] rounded-full text-lg font-semibold hover:bg-gray-100 transition"
              >
                Create a New Community
              </button>
              <button
                  onClick={handleJoinCommunity}
                  className="py-3 px-6 bg-transparent border border-white rounded-full text-lg font-semibold hover:bg-white hover:text-[#4b6cb7] transition"
              >
                Join an Existing Community
              </button>
            </div>
          </div>
        </div>

        {/* Features Section */}
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

        {/* Community Success Stories */}
        <div className="bg-white py-16">
          <div className="max-w-7xl mx-auto px-4">
            <h2 className="text-3xl font-bold text-center mb-12">Success Stories</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              {[
                { icon: MessageCircle, text: "Thanks to Neighborly, we organized a community garden that brings everyone together!", author: "Sarah Johnson", location: "Oak Street Community" },
                { icon: Handshake, text: "Found amazing neighbors who share tools and help each other out. It's like having an extended family!", author: "Mike Chen", location: "Maple Heights" },
                { icon: Heart, text: "Our neighborhood events have doubled in attendance since joining. The community spirit is amazing!", author: "Emma Martinez", location: "River Valley" }
              ].map((story, index) => (
                  <div key={index} className="bg-gray-50 rounded-xl p-6 shadow transition-transform hover:scale-105">
                    <story.icon className="w-8 h-8 text-[#4873AB] mb-4" />
                    <p className="text-gray-600 mb-4">"{story.text}"</p>
                    <p className="font-semibold">- {story.author}</p>
                    <p className="text-sm text-gray-500">{story.location}</p>
                  </div>
              ))}
            </div>
          </div>
        </div>

        {/* Call to Action */}
        <div className="bg-[#4873AB] py-16 text-white text-center">
          <h2 className="text-3xl font-extrabold mb-6">Ready to Get Started?</h2>
          <p className="text-lg mb-8">Join or create a community today and start making an impact in your neighborhood!</p>
          <button
              onClick={scrollToHero}
              className="py-3 px-6 bg-white text-[#4873AB] rounded-full text-lg font-semibold hover:bg-gray-100 transition"
          >
            Start Now
          </button>
        </div>
      </div>
  );
};

export default JoinOrCreate;
