import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';
import Navbar from './Navbar';

interface Project {
    id: number;
    name: string;
    description: string;
    status: string;
}

const Projects = () => {
    const [projects, setProjects] = useState<Project[]>([]);
    const navigate = useNavigate();

    const fetchProjects = async () => {
        try {
            const token = localStorage.getItem('token');
            const res = await axios.get('http://localhost:8080/projects', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setProjects(res.data);
        } catch (err) {
            console.error(err);
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
                <button onClick={() => navigate('/projects/new')}>Vytvořit nový projekt</button>
                <ul>
                    {projects.map(project => (
                        <li
                            key={project.id}
                            style={{ cursor: 'pointer', margin: '1rem 0' }}
                            onClick={() => navigate(`/projects/${project.id}/tickets`)}
                        >
                            {project.name} - {project.status}
                        </li>
                    ))}
                </ul>
            </div>
        </>
    );
};

export default Projects;
