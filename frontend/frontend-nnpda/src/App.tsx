import {Routes, Route, Navigate } from 'react-router';
import Login from './Login';
import Projects from './Projects';
import Tickets from './Tickets';
import TicketDetail from './TicketDetail';

const App = () => {
    const token = localStorage.getItem('token');

    return (
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/projects" element={token ? <Projects /> : <Navigate to="/login" />} />
                <Route path="/projects/:projectId/tickets" element={token ? <Tickets /> : <Navigate to="/login" />} />
                <Route path="/projects/:projectId/tickets/:ticketId" element={token ? <TicketDetail /> : <Navigate to="/login" />} />
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
    );
};

export default App;
