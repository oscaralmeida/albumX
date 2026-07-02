import { useCallback, useEffect, useState } from 'react';
import { api, type ApiError, type UserRanking } from '../api/apiClient';
import { ErrorMessage } from '../components/ErrorMessage';

export function RankingPage() {
  const [ranking, setRanking] = useState<UserRanking[]>([]);
  const [error, setError] = useState<ApiError | null>(null);
  const [loading, setLoading] = useState(false);

  const loadRanking = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getRanking();
      setRanking(data);
    } catch (e) {
      setError(e as ApiError);
      setRanking([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadRanking();
  }, [loadRanking]);

  return (
    <section>
      <h2>Ranking de colecionadores</h2>
      <p className="hint">
        Classificação por percentual de conclusão do álbum. Em caso de empate, quem tem mais trocas aceitas fica à frente.
      </p>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />

      <div className="card">
        <div className="card-header">
          <h3>Posições</h3>
          <button type="button" className="btn-secondary" onClick={loadRanking} disabled={loading}>
            Atualizar
          </button>
        </div>
        {loading ? (
          <p>Carregando ranking...</p>
        ) : ranking.length === 0 ? (
          <p className="empty-state">Nenhum colecionador cadastrado ainda.</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Posição</th>
                <th>Nome</th>
                <th>% Conclusão</th>
                <th>Figurinhas únicas</th>
                <th>Trocas aceitas</th>
              </tr>
            </thead>
            <tbody>
              {ranking.map((entry) => (
                <tr key={entry.userId}>
                  <td>#{entry.position}</td>
                  <td>{entry.userName}</td>
                  <td>{entry.albumCompletionPercentage.toFixed(2)}%</td>
                  <td>{entry.uniqueStickersCount}</td>
                  <td>{entry.acceptedTradesCount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}
