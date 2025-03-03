import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { render, screen, waitFor } from '@testing-library/react';
import axios from 'axios';
import Profile from '../pages/Profile';

jest.mock('axios');

describe('Profile Component', () => {
    it('should display loading initially', () => {
        render(
            <Router>
                <Profile />
            </Router>
        );
        expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('should fetch and display user details', async () => {
        const mockUser = {
            id: 31,
            name: 'krishna manager',
            email: 'krishnatejnk123@gmail.com',
            contact: null,
            neighbourhoodId: null,
            address: null,
            userType: 'USER',
            isEmailVerified: true,
        };

        axios.get.mockResolvedValue({ data: mockUser });

        render(
            <Router>
                <Profile />
            </Router>
        );

        await waitFor(() => expect(screen.getByText('krishna manager')).toBeInTheDocument());
        expect(screen.getByText('krishnatejnk123@gmail.com')).toBeInTheDocument();
    });
});