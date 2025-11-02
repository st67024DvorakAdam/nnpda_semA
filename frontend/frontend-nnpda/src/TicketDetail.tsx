import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router';
import Navbar from './Navbar';

interface Comment {
    id: number;
    text: string;
}

interface Ticket {
    id: number;
    title: string;
    state: string;
    priority: string;
    type: string;
    comments: Comment[];
}

const TicketDetail = () => {
    const { projectId, ticketId } = useParams();
    const [ticket, setTicket] = useState<Ticket | null>(null);
    const [newComment, setNewComment] = useState('');
    const navigate = useNavigate();

    const fetchTicket = async () => {
        try {
            const token = localStorage.getItem('token');
            const res = await axios.get(`http://localhost:8080/projects/${projectId}/tickets/${ticketId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setTicket(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const addComment = async () => {
        if (!newComment) return;
        try {
            const token = localStorage.getItem('token');
            await axios.post(
                `http://localhost:8080/projects/${projectId}/tickets/${ticketId}/comments`,
                { text: newComment },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setNewComment('');
            fetchTicket(); // reload
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchTicket();
    }, [projectId, ticketId]);

    if (!ticket) return <div>Načítám ticket...</div>;

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <button onClick={() => navigate(`/projects/${projectId}/tickets`)}>Zpět na tickety</button>
                <h2>{ticket.title}</h2>
                <p>Stav: {ticket.state}</p>
                <p>Priorita: {ticket.priority}</p>
                <p>Typ: {ticket.type}</p>

                <h3>Komentáře</h3>
                <ul>
                    {ticket.comments?.map(c => (
                        <li key={c.id}>{c.text}</li>
                    ))}
                </ul>

                <input
                    type="text"
                    value={newComment}
                    onChange={e => setNewComment(e.target.value)}
                    placeholder="Napsat komentář..."
                />
                <button onClick={addComment}>Přidat komentář</button>
            </div>
        </>
    );
};

export default TicketDetail;
