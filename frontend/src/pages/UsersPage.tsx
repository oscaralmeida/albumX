import { useState } from 'react';
import { api, type ApiError, type User } from '../api/apiClient';
import { ErrorMessage } from '../components/ErrorMessage';
import { UserForm } from '../components/UserForm';

interface UsersPageProps {
  users: User[];
  activeUserId: string | null;
  onUserCreated: (user: User) => void;
  onSelectUser: (userId: string) => void;
}

export function UsersPage({ users, activeUserId, onUserCreated, onSelectUser }: UsersPageProps) {
  const [error, setError] = useState<ApiError | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  async function handleCreate(name: string) {
    setError(null);
    setSuccess(null);
    try {
      const user = await api.createUser(name);
      onUserCreated(user);
      setSuccess(`Usuário "${user.name}" cadastrado com sucesso!`);
    } catch (e) {
      setError(e as ApiError);
    }
  }

  return (
    <section>
      <h2>Usuários</h2>
      <p className="hint">Cadastre colecionadores e selecione o usuário ativo para as demais operações.</p>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />
      {success && <div className="success-banner">{success}</div>}

      <UserForm onSubmit={handleCreate} />

      {users.length > 0 && (
        <div className="card">
          <h3>Colecionadores cadastrados</h3>
          <ul className="user-list">
            {users.map((user) => (
              <li key={user.id} className={user.id === activeUserId ? 'active' : ''}>
                <button type="button" className="user-select" onClick={() => onSelectUser(user.id)}>
                  <strong>{user.name}</strong>
                  <span className="user-id">{user.id}</span>
                  {user.id === activeUserId && <span className="badge">Ativo</span>}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
