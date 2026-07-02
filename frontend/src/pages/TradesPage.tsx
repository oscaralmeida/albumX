import { useCallback, useEffect, useState } from 'react';
import { api, type ApiError, type InitialTradeForm, type TradeProposal, type User } from '../api/apiClient';
import { ErrorMessage } from '../components/ErrorMessage';
import { TradeActions } from '../components/TradeActions';
import { TradeForm } from '../components/TradeForm';

interface TradesPageProps {
  users: User[];
  activeUserId: string | null;
  initialTradeForm?: InitialTradeForm | null;
}

function userName(users: User[], id: string): string {
  return users.find((u) => u.id === id)?.name ?? id.slice(0, 8) + '...';
}

function statusClass(status: string): string {
  switch (status) {
    case 'PROPOSED':
      return 'status-proposed';
    case 'ACCEPTED':
      return 'status-accepted';
    case 'REJECTED':
      return 'status-rejected';
    default:
      return '';
  }
}

function TradeTable({
  trades,
  users,
  activeUserId,
  showActions,
  actionLoading,
  onAccept,
  onReject,
}: {
  trades: TradeProposal[];
  users: User[];
  activeUserId: string | null;
  showActions: boolean;
  actionLoading: string | null;
  onAccept: (tradeId: string) => void;
  onReject: (tradeId: string) => void;
}) {
  if (trades.length === 0) {
    return <p className="empty-state">Nenhuma proposta nesta seção.</p>;
  }

  return (
    <table className="data-table">
      <thead>
        <tr>
          <th>Solicitante</th>
          <th>Destinatário</th>
          <th>Oferece</th>
          <th>Deseja</th>
          <th>Status</th>
          {showActions && <th>Ações</th>}
        </tr>
      </thead>
      <tbody>
        {trades.map((trade) => (
          <tr key={trade.id}>
            <td>{userName(users, trade.requesterUserId)}</td>
            <td>{userName(users, trade.targetUserId)}</td>
            <td>#{trade.offeredStickerNumber}</td>
            <td>#{trade.requestedStickerNumber}</td>
            <td>
              <span className={`badge status ${statusClass(trade.status)}`}>
                {trade.status}
              </span>
            </td>
            {showActions && (
              <td>
                {trade.status === 'PROPOSED' &&
                activeUserId === trade.targetUserId ? (
                  <TradeActions
                    tradeId={trade.id}
                    disabled={actionLoading === trade.id}
                    onAccept={onAccept}
                    onReject={onReject}
                  />
                ) : (
                  <span className="hint">—</span>
                )}
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export function TradesPage({ users, activeUserId, initialTradeForm }: TradesPageProps) {
  const [receivedTrades, setReceivedTrades] = useState<TradeProposal[]>([]);
  const [sentTrades, setSentTrades] = useState<TradeProposal[]>([]);
  const [error, setError] = useState<ApiError | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  const loadTrades = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [received, sent] = await Promise.all([
        activeUserId
          ? api.listTrades(undefined, activeUserId)
          : Promise.resolve([] as TradeProposal[]),
        activeUserId
          ? api.listTrades(activeUserId)
          : Promise.resolve([] as TradeProposal[]),
      ]);
      setReceivedTrades(received);
      setSentTrades(sent);
    } catch (e) {
      setError(e as ApiError);
    } finally {
      setLoading(false);
    }
  }, [activeUserId]);

  useEffect(() => {
    loadTrades();
  }, [loadTrades]);

  async function handleCreate(
    requesterUserId: string,
    targetUserId: string,
    offeredStickerNumber: number,
    requestedStickerNumber: number
  ) {
    setError(null);
    setSuccess(null);
    try {
      const trade = await api.createTrade(
        requesterUserId,
        targetUserId,
        offeredStickerNumber,
        requestedStickerNumber
      );
      setSuccess(
        `Proposta criada com status ${trade.status}: oferece #${trade.offeredStickerNumber}, deseja #${trade.requestedStickerNumber}.`
      );
      await loadTrades();
    } catch (e) {
      setError(e as ApiError);
    }
  }

  async function handleAccept(tradeId: string) {
    if (!activeUserId) return;
    setError(null);
    setSuccess(null);
    setActionLoading(tradeId);
    try {
      const trade = await api.acceptTrade(tradeId, activeUserId);
      setSuccess(`Proposta aceita! Status: ${trade.status}.`);
      await loadTrades();
    } catch (e) {
      setError(e as ApiError);
    } finally {
      setActionLoading(null);
    }
  }

  async function handleReject(tradeId: string) {
    if (!activeUserId) return;
    setError(null);
    setSuccess(null);
    setActionLoading(tradeId);
    try {
      const trade = await api.rejectTrade(tradeId, activeUserId);
      setSuccess(`Proposta recusada. Status: ${trade.status}.`);
      await loadTrades();
    } catch (e) {
      setError(e as ApiError);
    } finally {
      setActionLoading(null);
    }
  }

  if (users.length < 2) {
    return (
      <section>
        <h2>Trocas</h2>
        <p className="empty-state">Cadastre pelo menos dois colecionadores para criar propostas de troca.</p>
      </section>
    );
  }

  return (
    <section>
      <h2>Trocas</h2>
      <p className="hint">
        Crie propostas de troca ou responda às propostas recebidas. Selecione um usuário ativo para ver propostas enviadas e recebidas.
      </p>

      {!activeUserId && (
        <p className="hint">Selecione um colecionador na aba Usuários para aceitar ou recusar propostas.</p>
      )}

      <ErrorMessage error={error} onDismiss={() => setError(null)} />
      {success && <div className="success-banner">{success}</div>}

      <TradeForm
        users={users}
        activeUserId={activeUserId}
        initialTradeForm={initialTradeForm}
        onSubmit={handleCreate}
      />

      <div className="card">
        <div className="card-header">
          <h3>Propostas recebidas</h3>
          <button type="button" className="btn-secondary" onClick={loadTrades} disabled={loading}>
            Atualizar
          </button>
        </div>
        {loading ? (
          <p>Carregando...</p>
        ) : (
          <TradeTable
            trades={receivedTrades}
            users={users}
            activeUserId={activeUserId}
            showActions
            actionLoading={actionLoading}
            onAccept={handleAccept}
            onReject={handleReject}
          />
        )}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Propostas enviadas</h3>
        </div>
        {loading ? (
          <p>Carregando...</p>
        ) : (
          <TradeTable
            trades={sentTrades}
            users={users}
            activeUserId={activeUserId}
            showActions={false}
            actionLoading={null}
            onAccept={handleAccept}
            onReject={handleReject}
          />
        )}
      </div>
    </section>
  );
}
