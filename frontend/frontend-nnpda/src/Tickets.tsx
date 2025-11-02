import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router';
import Navbar from './Navbar';

interface Ticket {
    id: number;
    title: string;
    priority: string;
    state: string;
}

const Tickets = () => {
    const { projectId } = useParams();
    const [tickets, setTickets] = useState<Ticket[]>([]);
    const navigate = useNavigate();

    const fetchTickets = async () => {
        try {
            const token = localStorage.getItem('token');
            const res = await axios.get(`http://localhost:8080/projects/${projectId}/tickets`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setTickets(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchTickets();
    }, [projectId]);

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <button onClick={() => navigate('/projects')}>Zpět na projekty</button>
                <h2>Tickety projektu {projectId}</h2>
                <ul>
                    {tickets.map(ticket => (
                        <li
                            key={ticket.id}
                            style={{ cursor: 'pointer', margin: '1rem 0' }}
                            onClick={() => navigate(`/projects/${projectId}/tickets/${ticket.id}`)}
                        >
                            {ticket.title} - {ticket.state} ({ticket.priority})
                        </li>
                    ))}
                </ul>
            </div>
        </>
    );
};

export default Tickets;
