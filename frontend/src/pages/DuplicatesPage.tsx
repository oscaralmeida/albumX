import { useCallback, useEffect, useState } from 'react';
import { api, type ApiError, type CollectionEntry } from '../api/apiClient';
import { CollectionList } from '../components/CollectionList';
import { ErrorMessage } from '../components/ErrorMessage';

interface DuplicatesPageProps {
  activeUserId: string | null;
  activeUserName: string | null;
}

export function DuplicatesPage({ activeUserId, activeUserName }: DuplicatesPageProps) {
  const [duplicates, setDuplicates] = useState<CollectionEntry[]>([]);
  const [error, setError] = useState<ApiError | null>(null);
  const [loading, setLoading] = useState(false);

  const loadDuplicates = useCallback(async () => {
    if (!activeUserId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await api.getDuplicates(activeUserId);
      setDuplicates(data);
    } catch (e) {
      setError(e as ApiError);
    } finally {
      setLoading(false);
    }
  }, [activeUserId]);

  useEffect(() => {
    loadDuplicates();
  }, [loadDuplicates]);

  if (!activeUserId) {
    return (
      <section>
        <h2>Repetidas</h2>
        <p className="empty-state">Selecione um usuário ativo na aba Usuários.</p>
      </section>
    );
  }

  return (
    <section>
      <h2>Repetidas — {activeUserName}</h2>
      <p className="hint">Figurinhas com quantidade maior que 1 na coleção do usuário ativo.</p>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />

      <div className="card">
        <div className="card-header">
          <h3>Figurinhas repetidas</h3>
          <button type="button" className="btn-secondary" onClick={loadDuplicates} disabled={loading}>
            Atualizar
          </button>
        </div>
        {loading ? (
          <p>Carregando...</p>
        ) : (
          <CollectionList
            entries={duplicates}
            emptyMessage="Nenhuma figurinha repetida. Adicione a mesma figurinha mais de uma vez na coleção."
          />
        )}
      </div>
    </section>
  );
}
