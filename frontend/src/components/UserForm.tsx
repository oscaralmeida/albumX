import { FormEvent, useState } from 'react';

interface UserFormProps {
  onSubmit: (name: string) => Promise<void>;
  disabled?: boolean;
}

export function UserForm({ onSubmit, disabled }: UserFormProps) {
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    setLoading(true);
    try {
      await onSubmit(name.trim());
      setName('');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <h3>Cadastrar colecionador</h3>
      <label htmlFor="user-name">Nome</label>
      <input
        id="user-name"
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Ex.: Maria Colecionadora"
        disabled={disabled || loading}
      />
      <button type="submit" disabled={disabled || loading || !name.trim()}>
        {loading ? 'Cadastrando...' : 'Cadastrar'}
      </button>
    </form>
  );
}
