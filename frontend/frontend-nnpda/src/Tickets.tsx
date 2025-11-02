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

interface Project {
    id: number;
    name: string;
    description: string;
    status: 'ACTIVE' | 'ARCHIVED';
}

interface Comment {
    id: number;
    text: string;
    author: { username: string };
    createdAt: string;
}

interface Attachment {
    id: number;
    filename: string;
    uploader: { username: string };
}

const Tickets = () => {
    const { projectId } = useParams();
    const [project, setProject] = useState<Project | null>(null);
    const [tickets, setTickets] = useState<Ticket[]>([]);
    const [comments, setComments] = useState<Comment[]>([]);
    const [attachments, setAttachments] = useState<Attachment[]>([]);
    const [newComment, setNewComment] = useState('');
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const navigate = useNavigate();

    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    // Načti projekt + tickety + komentáře + přílohy
    useEffect(() => {
        if (!projectId) return;

        const fetchAll = async () => {
            try {
                const [projRes, ticketRes, commentRes, attachRes] = await Promise.all([
                    axios.get(`http://localhost:8080/projects/${projectId}`, { headers }),
                    axios.get(`http://localhost:8080/projects/${projectId}/tickets`, { headers }),
                    axios.get(`http://localhost:8080/projects/${projectId}/comments`, { headers }),
                    axios.get(`http://localhost:8080/projects/${projectId}/attachments`, { headers }),
                ]);

                setProject(projRes.data);
                setTickets(ticketRes.data);
                // Komentáře od nejstaršího k nejnovějšímu
                setComments(commentRes.data.sort((a: Comment, b: Comment) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()));
                setAttachments(attachRes.data);
            } catch (err) {
                console.error(err);
            }
        };

        fetchAll();
    }, [projectId]);

    const handleAddComment = async () => {
        if (!newComment.trim()) return;
        try {
            const res = await axios.post(
                `http://localhost:8080/projects/${projectId}/comments`,
                { text: newComment },
                { headers }
            );
            setComments(prev => [...prev, res.data]);
            setNewComment('');
        } catch (err) {
            console.error(err);
            alert('Nepodařilo se přidat komentář.');
        }
    };

    const handleDeleteComment = async (commentId: number) => {
        if (!window.confirm('Opravdu smazat komentář?')) return;
        try {
            await axios.delete(`http://localhost:8080/projects/${projectId}/comments/${commentId}`, { headers });
            setComments(prev => prev.filter(c => c.id !== commentId));
        } catch (err) {
            console.error(err);
        }
    };

    const handleFileUpload = async () => {
        if (!selectedFile) return;
        const formData = new FormData();
        formData.append('file', selectedFile);
        try {
            const res = await axios.post(
                `http://localhost:8080/projects/${projectId}/attachments`,
                formData,
                { headers: { ...headers, 'Content-Type': 'multipart/form-data' } }
            );
            setAttachments(prev => [...prev, res.data]);
            setSelectedFile(null);
        } catch (err) {
            console.error(err);
        }
    };

    const handleDeleteAttachment = async (id: number) => {
        if (!window.confirm('Opravdu smazat přílohu?')) return;
        try {
            await axios.delete(`http://localhost:8080/projects/${projectId}/attachments/${id}`, { headers });
            setAttachments(prev => prev.filter(a => a.id !== id));
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <button onClick={() => navigate('/projects')}>⬅ Zpět na projekty</button>

                {project && (
                    <div style={{
                        marginTop: '1rem',
                        padding: '1rem',
                        border: '1px solid #ccc',
                        borderRadius: '8px',
                        backgroundColor: '#f9f9f9'
                    }}>
                        <h2 style={{ margin: 0, color: '#555' }}>{project.name}</h2>
                        <p style={{ margin: 0, color: '#555' }}>{project.description}</p>
                        <p style={{ margin: 0, color: '#555' }}><strong>Status:</strong> {project.status}</p>
                    </div>
                )}

                <h3 style={{ marginTop: '2rem' }}>Tickety</h3>
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

                {/* KOMENTÁŘE */}
                <h3>Komentáře</h3>
                <div>
                    {comments.map(comment => (
                        <div
                            key={comment.id}
                            style={{
                                borderBottom: '1px solid #ddd',
                                padding: '0.5rem 0'
                            }}
                        >
                            <p><strong>{comment.author.username}</strong> ({new Date(comment.createdAt).toLocaleString()})</p>
                            <p>{comment.text}</p>
                            <button
                                onClick={() => handleDeleteComment(comment.id)}
                                style={{
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    padding: '0.3rem 0.6rem',
                                    cursor: 'pointer'
                                }}
                            >
                                Smazat
                            </button>
                        </div>
                    ))}
                    <textarea
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        placeholder="Napište komentář..."
                        style={{ width: '100%', marginTop: '1rem', padding: '0.5rem' }}
                    />
                    <button
                        onClick={handleAddComment}
                        style={{
                            marginTop: '0.5rem',
                            backgroundColor: '#007bff',
                            color: 'white',
                            border: 'none',
                            padding: '0.5rem 1rem',
                            borderRadius: '5px'
                        }}
                    >
                        💬 Přidat komentář
                    </button>
                </div>

                {/* PŘÍLOHY */}
                <h3 style={{ marginTop: '2rem' }}>Přílohy</h3>
                <ul>
                    {attachments.map(a => (
                        <li key={a.id} style={{ marginBottom: '0.5rem' }}>
            <span
                onClick={async () => {
                    try {
                        const res = await axios.get(
                            `http://localhost:8080/attachments/${a.id}/download`,
                            { headers, responseType: 'blob' } // velmi důležité!
                        );
                        const file = new Blob([res.data], { type: res.data.type || 'application/octet-stream' });
                        const url = window.URL.createObjectURL(file);
                        window.open(url, '_blank');
                    } catch (err) {
                        console.error(err);
                        alert('Nepodařilo se otevřít přílohu.');
                    }
                }}
                style={{ cursor: 'pointer', color: 'blue', textDecoration: 'underline' }}
            >
                {a.filename}
            </span>{' '}
                            <small>({a.uploader.username})</small>
                            <button
                                onClick={() => handleDeleteAttachment(a.id)}
                                style={{
                                    marginLeft: '1rem',
                                    backgroundColor: '#dc3545',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    padding: '0.3rem 0.6rem',
                                    cursor: 'pointer'
                                }}
                            >
                                🗑️
                            </button>
                        </li>
                    ))}
                </ul>

                <div style={{ marginTop: '1rem' }}>
                    <input type="file" onChange={(e) => setSelectedFile(e.target.files?.[0] || null)} />
                    <button
                        onClick={handleFileUpload}
                        style={{
                            marginLeft: '0.5rem',
                            backgroundColor: '#28a745',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            padding: '0.5rem 1rem',
                            cursor: 'pointer'
                        }}
                    >
                        ⬆️ Nahrát
                    </button>
                </div>
            </div>
        </>
    );
};

export default Tickets;
