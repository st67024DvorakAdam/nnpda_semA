import { Routes, Route, Navigate } from 'react-router';
import Login from './Login';
import Register from './Register';
import Projects from './Projects';
import Tickets from './Tickets';
import TicketDetail from './TicketDetail';
import CreateProject from "./CreateProject.tsx";
import CreateTicket from "./CreateTicket.tsx";
import AssignedTickets from "./AssignedTickets.tsx";
import ElkTicketsSearch from "./ElkTicketsSearch.tsx";

const App = () => {
    const token = localStorage.getItem('token');

    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/projects" element={token ? <Projects /> : <Navigate to="/login" />} />
            <Route path="/projects/new" element={<CreateProject />} />
            <Route path="/projects/:id/edit" element={<CreateProject />} />
            <Route path="/projects/:projectId/tickets" element={token ? <Tickets /> : <Navigate to="/login" />} />
            <Route path="/projects/:projectId/tickets/create" element={<CreateTicket />} />
            <Route path="/projects/:projectId/tickets/:ticketId/edit" element={<CreateTicket />} />
            <Route path="/projects/:projectId/tickets/:ticketId" element={token ? <TicketDetail /> : <Navigate to="/login" />} />
            <Route path="/assigned-tickets" element={<AssignedTickets />} />
            <Route path="/elk-ticket-search" element={<ElkTicketsSearch />} />
            <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
    );
};

export default App;
