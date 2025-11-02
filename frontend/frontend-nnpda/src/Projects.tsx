import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';
import Navbar from './Navbar';

interface Project {
    id: number;
    name: string;
    description: string;
    status: 'ACTIVE' | 'ARCHIVED';
}

const Projects: React.FC = () => {
    const [projects, setProjects] = useState<Project[]>([]);
    const navigate = useNavigate();

    const fetchProjects = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                navigate('/login');
                return;
            }

            const res = await axios.get<Project[]>('http://localhost:8080/projects', {
                headers: { Authorization: `Bearer ${token}` },
            });

            setProjects(res.data);
        } catch (err) {
            console.error('Chyba při načítání projektů:', err);
            alert('Nepodařilo se načíst projekty. Přihlaste se znovu.');
            navigate('/login');
        }
    };

    const handleDelete = async (projectId: number) => {
        const confirmed = window.confirm('Opravdu chcete tento projekt smazat?');
        if (!confirmed) return;

        try {
            const token = localStorage.getItem('token');
            await axios.delete(`http://localhost:8080/projects/${projectId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setProjects(prev => prev.filter(p => p.id !== projectId));
        } catch (err) {
            console.error('Chyba při mazání projektu:', err);
            alert('Nepodařilo se smazat projekt.');
        }
    };

    useEffect(() => {
        fetchProjects();
    }, []);

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem' }}>
                <h2>Projekty</h2>
                <button
                    onClick={() => navigate('/projects/new')}
                    style={{
                        marginBottom: '1rem',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        padding: '0.5rem 1rem',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    + Vytvořit nový projekt
                </button>

                {projects.length === 0 ? (
                    <p>Žádné projekty k zobrazení.</p>
                ) : (
                    <ul style={{ listStyle: 'none', padding: 0 }}>
                        {projects.map(project => (
                            <li
                                key={project.id}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    margin: '1rem 0',
                                    padding: '1rem',
                                    border: '1px solid #ccc',
                                    borderRadius: '8px',
                                    backgroundColor: '#f9f9f9'
                                }}
                            >
                                <div
                                    onClick={() => navigate(`/projects/${project.id}/tickets`)}
                                    style={{ cursor: 'pointer', flexGrow: 1 }}
                                >
                                    <strong style={{ margin: 0, color: '#555' }}>{project.name} – {project.status}</strong>
                                    <p style={{ margin: 0, color: '#555' }}>{project.description}</p>
                                </div>
                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                    <button
                                        onClick={() => navigate(`/projects/${project.id}/edit`)}
                                        style={{
                                            backgroundColor: '#ffc107',
                                            border: 'none',
                                            padding: '0.4rem 0.8rem',
                                            borderRadius: '5px',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        ✏️ Upravit
                                    </button>
                                    <button
                                        onClick={() => handleDelete(project.id)}
                                        style={{
                                            backgroundColor: '#dc3545',
                                            color: 'white',
                                            border: 'none',
                                            padding: '0.4rem 0.8rem',
                                            borderRadius: '5px',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        🗑️ Smazat
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </>
    );
};

export default Projects;
