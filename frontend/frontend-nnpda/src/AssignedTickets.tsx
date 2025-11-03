import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';
import Navbar from './Navbar';

interface TicketDto {
    id: number;
    title: string;
    type: string;
    priority: string;
    state: string;
    assignee?: {
        id: number;
        username: string;
        email: string;
    };
}

interface Project {
    id: number;
    name: string;
}

const AssignedTickets = () => {
    const [tickets, setTickets] = useState<TicketDto[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    useEffect(() => {
        const fetchAssignedTickets = async () => {
            try {
                // 1. Zjisti všechny projekty, ke kterým má uživatel přístup
                const projectsRes = await axios.get<Project[]>('http://localhost:8080/projects', { headers });

                // 2. Získat tickety ze všech projektů
                const allTickets: TicketDto[] = [];

                for (const project of projectsRes.data) {
                    const res = await axios.get<TicketDto[]>(
                        `http://localhost:8080/projects/${project.id}/tickets`,
                        { headers }
                    );
                    // přidáme informaci o projektu (kvůli přesměrování)
                    const projectTickets = res.data.map(t => ({ ...t, projectId: project.id }));
                    allTickets.push(...projectTickets);
                }

                // 3. Filtrovat tickety, kde je přihlášený uživatel řešitelem
                const userInfo = JSON.parse(atob(token!.split('.')[1]));
                const username = userInfo.sub;

                const assigned = allTickets.filter(t => t.assignee && t.assignee.username === username);

                setTickets(assigned);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchAssignedTickets();
    }, []);

    if (loading) return <div>Načítám tickety...</div>;

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <h2>Mně přiřazené tickety</h2>
                {tickets.length === 0 ? (
                    <p>Nemáš přiřazené žádné tickety.</p>
                ) : (
                    <table
                        style={{
                            width: '100%',
                            borderCollapse: 'collapse',
                            marginTop: '1rem',
                            border: '1px solid #ddd'
                        }}
                    >
                        <thead style={{ backgroundColor: '#000' }}>
                        <tr>
                            <th style={{ padding: '0.5rem', border: '1px solid #ddd' }}>ID</th>
                            <th style={{ padding: '0.5rem', border: '1px solid #ddd' }}>Název</th>
                            <th style={{ padding: '0.5rem', border: '1px solid #ddd' }}>Typ</th>
                            <th style={{ padding: '0.5rem', border: '1px solid #ddd' }}>Priorita</th>
                            <th style={{ padding: '0.5rem', border: '1px solid #ddd' }}>Stav</th>
                        </tr>
                        </thead>
                        <tbody>
                        {tickets.map(ticket => (
                            <tr
                                key={ticket.id}
                                onClick={() =>
                                    navigate(`/projects/${(ticket as any).projectId}/tickets/${ticket.id}`)
                                }
                                style={{
                                    cursor: 'pointer',
                                    borderBottom: '1px solid #ddd',
                                    backgroundColor: '#000'
                                }}
                            >
                                <td style={{ padding: '0.5rem', border: '1px solid #ddd' }}>{ticket.id}</td>
                                <td style={{ padding: '0.5rem', border: '1px solid #ddd' }}>{ticket.title}</td>
                                <td style={{ padding: '0.5rem', border: '1px solid #ddd' }}>{ticket.type}</td>
                                <td style={{ padding: '0.5rem', border: '1px solid #ddd' }}>{ticket.priority}</td>
                                <td style={{ padding: '0.5rem', border: '1px solid #ddd' }}>{ticket.state}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </>
    );
};

export default AssignedTickets;
