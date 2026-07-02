import { useCallback, useEffect, useState } from 'react';
import { api, type ApiError, type CollectionEntry } from '../api/apiClient';
import { CollectionList } from '../components/CollectionList';
import { ErrorMessage } from '../components/ErrorMessage';
import { StickerForm } from '../components/StickerForm';

interface CollectionPageProps {
  activeUserId: string | null;
  activeUserName: string | null;
}

export function CollectionPage({ activeUserId, activeUserName }: CollectionPageProps) {
  const [entries, setEntries] = useState<CollectionEntry[]>([]);
  const [error, setError] = useState<ApiError | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const loadCollection = useCallback(async () => {
    if (!activeUserId) return;
    setLoading(true);
    setError(null);
    try {
      const collection = await api.getCollection(activeUserId);
      setEntries(collection.entries);
    } catch (e) {
      setError(e as ApiError);
    } finally {
      setLoading(false);
    }
  }, [activeUserId]);

  useEffect(() => {
    loadCollection();
  }, [loadCollection]);

  async function handleAddSticker(stickerNumber: number) {
    if (!activeUserId) return;
    setError(null);
    setSuccess(null);
    try {
      const entry = await api.addSticker(activeUserId, stickerNumber);
      setSuccess(`Figurinha #${entry.stickerNumber} adicionada (quantidade: ${entry.quantity}).`);
      await loadCollection();
    } catch (e) {
      setError(e as ApiError);
    }
  }

  if (!activeUserId) {
    return (
      <section>
        <h2>Coleção</h2>
        <p className="empty-state">Selecione um usuário ativo na aba Usuários.</p>
      </section>
    );
  }

  return (
    <section>
      <h2>Coleção — {activeUserName}</h2>
      <p className="hint">Adicione figurinhas à coleção do usuário ativo. Cada adição incrementa a quantidade.</p>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />
      {success && <div className="success-banner">{success}</div>}

      <StickerForm onSubmit={handleAddSticker} />

      <div className="card">
        <div className="card-header">
          <h3>Figurinhas na coleção</h3>
          <button type="button" className="btn-secondary" onClick={loadCollection} disabled={loading}>
            Atualizar
          </button>
        </div>
        {loading ? <p>Carregando...</p> : <CollectionList entries={entries} />}
      </div>
    </section>
  );
}
