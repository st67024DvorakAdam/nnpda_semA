import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router';
import Navbar from './Navbar';

export const TicketType = {
    BUG: 'BUG',
    FEATURE: 'FEATURE',
    TASK: 'TASK'
} as const;
export type TicketType = (typeof TicketType)[keyof typeof TicketType];

export const TicketPriority = {
    LOW: 'LOW',
    MED: 'MED',
    HIGH: 'HIGH'
} as const;
export type TicketPriority = (typeof TicketPriority)[keyof typeof TicketPriority];

export const TicketState = {
    OPEN: 'OPEN',
    IN_PROGRESS: 'IN_PROGRESS',
    DONE: 'DONE'
} as const;
export type TicketState = (typeof TicketState)[keyof typeof TicketState];

interface TicketDto {
    id?: number;
    title: string;
    type: TicketType;
    priority: TicketPriority;
    state?: TicketState;
    assigneeId?: number;
}

interface AppUser {
    id: number;
    username: string;
}

const CreateTicket = () => {
    const { projectId, ticketId } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    const [ticket, setTicket] = useState<TicketDto>({
        title: '',
        type: TicketType.BUG,
        priority: TicketPriority.LOW,
        state: TicketState.OPEN,
        assigneeId: undefined
    });

    const [users, setUsers] = useState<AppUser[]>([]);

    useEffect(() => {
        // načti všechny uživatele
        const fetchUsers = async () => {
            try {
                const res = await axios.get('http://localhost:8080/users', { headers });
                setUsers(res.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchUsers();
    }, []);

    useEffect(() => {
        if (!ticketId) return; // nový ticket
        const fetchTicket = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/projects/${projectId}/tickets/${ticketId}`, { headers });
                setTicket(res.data);
            } catch (err) {
                console.error(err);
                alert('Nepodařilo se načíst ticket.');
            }
        };
        fetchTicket();
    }, [projectId, ticketId]);

    const handleSubmit = async () => {
        try {
            if (ticketId) {
                await axios.put(
                    `http://localhost:8080/projects/${projectId}/tickets/${ticketId}`,
                    ticket,
                    { headers }
                );
            } else {
                await axios.post(
                    `http://localhost:8080/projects/${projectId}/tickets`,
                    ticket,
                    { headers }
                );
            }
            navigate(`/projects/${projectId}/tickets`);
        } catch (err) {
            console.error(err);
            alert('Nepodařilo se uložit ticket.');
        }
    };

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <h2>{ticketId ? 'Upravit ticket' : 'Nový ticket'}</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px' }}>
                    <input
                        type="text"
                        placeholder="Název ticketu"
                        value={ticket.title}
                        onChange={(e) => setTicket({ ...ticket, title: e.target.value })}
                    />

                    <select
                        value={ticket.type}
                        onChange={(e) => setTicket({ ...ticket, type: e.target.value as TicketType })}
                    >
                        {Object.values(TicketType).map(t => (
                            <option key={t} value={t}>{t}</option>
                        ))}
                    </select>

                    <select
                        value={ticket.priority}
                        onChange={(e) => setTicket({ ...ticket, priority: e.target.value as TicketPriority })}
                    >
                        {Object.values(TicketPriority).map(p => (
                            <option key={p} value={p}>{p}</option>
                        ))}
                    </select>

                    {ticketId && (
                        <select
                            value={ticket.state}
                            onChange={(e) => setTicket({ ...ticket, state: e.target.value as TicketState })}
                        >
                            {Object.values(TicketState).map(s => (
                                <option key={s} value={s}>{s}</option>
                            ))}
                        </select>
                    )}

                    <select
                        value={ticket.assigneeId || ''}
                        onChange={(e) => setTicket({ ...ticket, assigneeId: e.target.value ? parseInt(e.target.value) : undefined })}
                    >
                        <option value="">-- Žádný řešitel --</option>
                        {users.map(u => (
                            <option key={u.id} value={u.id}>{u.username}</option>
                        ))}
                    </select>

                    <button
                        onClick={handleSubmit}
                        style={{
                            backgroundColor: '#007bff',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            padding: '0.5rem 1rem',
                            cursor: 'pointer'
                        }}
                    >
                        {ticketId ? 'Uložit změny' : 'Vytvořit ticket'}
                    </button>

                    <button
                        onClick={() => navigate(`/projects/${projectId}/tickets`)}
                        style={{
                            marginTop: '0.5rem',
                            backgroundColor: '#6c757d',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            padding: '0.5rem 1rem',
                            cursor: 'pointer'
                        }}
                    >
                        Zrušit
                    </button>
                </div>
            </div>
        </>
    );
};

export default CreateTicket;
