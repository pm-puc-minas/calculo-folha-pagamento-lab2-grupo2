import React, { useState } from 'react';
import './LoginPage.css';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
    });
    const navigate = useNavigate();

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            const response = await api.post('/api/auth/login', {
                login: formData.email,
                senha: formData.password
            });

            if (response.status === 200) {
                localStorage.setItem('auth', 'true');
                if (response.data) {
                    localStorage.setItem('user', JSON.stringify(response.data));
                }
                navigate('/', { replace: true });
            }
        } catch (error) {
            if (error.response) {
                alert(`Erro no login: ${error.response.data}`);
            } else {
                console.error('Erro de rede ou de conexão:', error);
                alert('Não foi possível conectar ao servidor. Verifique se a API está sendo executada.');
            }
        }
    };

    return (
        <div className="login-page">
            <div className="login-container">
                <h1>Login</h1>
                <p className="subtitle">Bem vindo(a) de volta! 👋</p>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input
                            type="text"
                            id="email"
                            name="email"
                            placeholder="johnDoe@domain.com"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Senha</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Digite aqui sua senha"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="options-group">
                        <div className="remember-me">
                            <input type="checkbox" id="remember" />
                            <label htmlFor="remember">Lembrar de mim?</label>
                        </div>
                        <a href="#" className="forgot-password">Esqueceu sua senha?</a>
                    </div>

                    <button type="submit" className="login-button">Entrar</button>
                </form>

                <p className="signup-link">
                    Não se cadastrou ainda? <a href="#">Não perca tempo! ↗</a>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;