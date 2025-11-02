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

            // backend vrací přímo pole projektů
            setProjects(res.data);

        } catch (err) {
            console.error('Chyba při načítání projektů:', err);
            alert('Nepodařilo se načíst projekty. Přihlaste se znovu.');
            navigate('/login');
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
                    style={{ marginBottom: '1rem' }}
                >
                    Vytvořit nový projekt
                </button>

                {projects.length === 0 ? (
                    <p>Žádné projekty k zobrazení.</p>
                ) : (
                    <ul>
                        {projects.map(project => (
                            <li
                                key={project.id}
                                style={{
                                    cursor: 'pointer',
                                    margin: '1rem 0',
                                    padding: '0.5rem',
                                    border: '1px solid #ccc',
                                    borderRadius: '5px',
                                }}
                                onClick={() => navigate(`/projects/${project.id}/tickets`)}
                            >
                                <strong>{project.name}</strong> - {project.status}
                                <p>{project.description}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </>
    );
};

export default Projects;
