import { useCallback, useEffect, useState } from 'react';
import { api, type ApiError, type TradeSuggestion, type User } from '../api/apiClient';
import { ErrorMessage } from '../components/ErrorMessage';
import { SuggestionList } from '../components/SuggestionList';

interface SuggestionsPageProps {
  users: User[];
  activeUserId: string | null;
  onCreateProposalFromSuggestion: (suggestion: TradeSuggestion) => void;
}

export function SuggestionsPage({
  users,
  activeUserId,
  onCreateProposalFromSuggestion,
}: SuggestionsPageProps) {
  const [suggestions, setSuggestions] = useState<TradeSuggestion[]>([]);
  const [error, setError] = useState<ApiError | null>(null);
  const [loading, setLoading] = useState(false);

  const loadSuggestions = useCallback(async () => {
    if (!activeUserId) {
      setSuggestions([]);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const data = await api.getTradeSuggestions(activeUserId);
      setSuggestions(data);
    } catch (e) {
      setError(e as ApiError);
      setSuggestions([]);
    } finally {
      setLoading(false);
    }
  }, [activeUserId]);

  useEffect(() => {
    loadSuggestions();
  }, [loadSuggestions]);

  if (users.length < 2) {
    return (
      <section>
        <h2>Sugestões</h2>
        <p className="empty-state">Cadastre pelo menos dois colecionadores para ver sugestões de troca.</p>
      </section>
    );
  }

  return (
    <section>
      <h2>Sugestões automáticas</h2>
      <p className="hint">
        Oportunidades de troca mútua calculadas com base nas coleções. As sugestões são apenas consultivas — nenhuma proposta é criada automaticamente.
      </p>

      {!activeUserId && (
        <p className="hint">Selecione um colecionador na aba Usuários para consultar sugestões na perspectiva dele.</p>
      )}

      <ErrorMessage error={error} onDismiss={() => setError(null)} />

      {activeUserId && (
        <div className="card">
          <div className="card-header">
            <h3>Sugestões para o usuário ativo</h3>
            <button type="button" className="btn-secondary" onClick={loadSuggestions} disabled={loading}>
              Atualizar
            </button>
          </div>
          {loading ? (
            <p>Carregando sugestões...</p>
          ) : (
            <SuggestionList
              suggestions={suggestions}
              users={users}
              onCreateProposal={onCreateProposalFromSuggestion}
            />
          )}
        </div>
      )}
    </section>
  );
}
