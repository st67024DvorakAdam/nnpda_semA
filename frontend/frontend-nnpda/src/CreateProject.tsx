import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router';
import axios from 'axios';
import Navbar from './Navbar';

interface Project {
    id?: number;
    name: string;
    description: string;
    status?: 'ACTIVE' | 'ARCHIVED';
}

const CreateProject: React.FC = () => {
    const { id } = useParams<{ id: string }>(); // pokud existuje → editace
    const navigate = useNavigate();

    const [project, setProject] = useState<Project>({
        name: '',
        description: '',
        status: 'ACTIVE',
    });

    const [loading, setLoading] = useState(false);
    const isEditMode = !!id;

    // pokud je editace, načteme projekt
    useEffect(() => {
        const fetchProject = async () => {
            if (!isEditMode) return;
            try {
                const token = localStorage.getItem('token');
                const res = await axios.get<Project>(`http://localhost:8080/projects/${id}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setProject(res.data);
            } catch (err) {
                console.error('Chyba při načítání projektu:', err);
                alert('Nepodařilo se načíst projekt.');
                navigate('/projects');
            }
        };

        fetchProject();
    }, [id, isEditMode, navigate]);

    // odeslání formuláře (vytvoření nebo úprava)
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            const token = localStorage.getItem('token');
            const config = {
                headers: { Authorization: `Bearer ${token}` },
            };

            if (isEditMode) {
                await axios.put(`http://localhost:8080/projects/${id}`, project, config);
                alert('Projekt byl úspěšně upraven.');
            } else {
                await axios.post('http://localhost:8080/projects', {
                    name: project.name,
                    description: project.description,
                }, config);
                alert('Projekt byl úspěšně vytvořen.');
            }

            navigate('/projects');
        } catch (err) {
            console.error('Chyba při odesílání projektu:', err);
            alert('Operace se nezdařila.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Navbar />
            <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
                <h2>{isEditMode ? 'Upravit projekt' : 'Vytvořit nový projekt'}</h2>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div>
                        <label htmlFor="name">Název projektu:</label>
                        <input
                            id="name"
                            type="text"
                            value={project.name}
                            onChange={(e) => setProject({ ...project, name: e.target.value })}
                            required
                            style={{
                                width: '100%',
                                padding: '0.5rem',
                                borderRadius: '5px',
                                border: '1px solid #ccc',
                            }}
                        />
                    </div>

                    <div>
                        <label htmlFor="description">Popis:</label>
                        <textarea
                            id="description"
                            value={project.description}
                            onChange={(e) => setProject({ ...project, description: e.target.value })}
                            style={{
                                width: '100%',
                                minHeight: '100px',
                                padding: '0.5rem',
                                borderRadius: '5px',
                                border: '1px solid #ccc',
                            }}
                        />
                    </div>

                    {isEditMode && (
                        <div>
                            <label htmlFor="status">Stav projektu:</label>
                            <select
                                id="status"
                                value={project.status}
                                onChange={(e) =>
                                    setProject({ ...project, status: e.target.value as 'ACTIVE' | 'ARCHIVED' })
                                }
                                style={{
                                    width: '100%',
                                    padding: '0.5rem',
                                    borderRadius: '5px',
                                    border: '1px solid #ccc',
                                }}
                            >
                                <option value="ACTIVE">ACTIVE</option>
                                <option value="ARCHIVED">ARCHIVED</option>
                            </select>
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            backgroundColor: '#007bff',
                            color: 'white',
                            border: 'none',
                            padding: '0.6rem 1rem',
                            borderRadius: '5px',
                            cursor: 'pointer',
                        }}
                    >
                        {loading
                            ? 'Ukládám...'
                            : isEditMode
                                ? 'Uložit změny'
                                : 'Vytvořit projekt'}
                    </button>

                    <button
                        type="button"
                        onClick={() => navigate('/projects')}
                        style={{
                            backgroundColor: '#6c757d',
                            color: 'white',
                            border: 'none',
                            padding: '0.6rem 1rem',
                            borderRadius: '5px',
                            cursor: 'pointer',
                        }}
                    >
                        Zpět na seznam
                    </button>
                </form>
            </div>
        </>
    );
};

export default CreateProject;
