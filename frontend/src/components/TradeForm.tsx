import { FormEvent, useEffect, useState } from 'react';
import type { InitialTradeForm, User } from '../api/apiClient';

interface TradeFormProps {
  users: User[];
  activeUserId: string | null;
  initialTradeForm?: InitialTradeForm | null;
  onSubmit: (
    requesterUserId: string,
    targetUserId: string,
    offeredStickerNumber: number,
    requestedStickerNumber: number
  ) => Promise<void>;
  disabled?: boolean;
}

export function TradeForm({ users, activeUserId, initialTradeForm, onSubmit, disabled }: TradeFormProps) {
  const [requesterId, setRequesterId] = useState(activeUserId ?? '');
  const [targetId, setTargetId] = useState('');
  const [offered, setOffered] = useState('');
  const [requested, setRequested] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialTradeForm) {
      setRequesterId(initialTradeForm.requesterUserId);
      setTargetId(initialTradeForm.targetUserId);
      setOffered(String(initialTradeForm.offeredStickerNumber));
      setRequested(String(initialTradeForm.requestedStickerNumber));
    }
  }, [initialTradeForm]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const offeredNum = parseInt(offered, 10);
    const requestedNum = parseInt(requested, 10);
    if (!requesterId || !targetId || isNaN(offeredNum) || isNaN(requestedNum)) return;

    setLoading(true);
    try {
      await onSubmit(requesterId, targetId, offeredNum, requestedNum);
      setOffered('');
      setRequested('');
    } finally {
      setLoading(false);
    }
  }

  const otherUsers = users.filter((u) => u.id !== requesterId);

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <h3>Nova proposta de troca</h3>

      <label htmlFor="trade-requester">Solicitante</label>
      <select
        id="trade-requester"
        value={requesterId}
        onChange={(e) => {
          setRequesterId(e.target.value);
          setTargetId('');
        }}
        disabled={disabled || loading}
      >
        <option value="">Selecione...</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name}
          </option>
        ))}
      </select>

      <label htmlFor="trade-target">Destinatário</label>
      <select
        id="trade-target"
        value={targetId}
        onChange={(e) => setTargetId(e.target.value)}
        disabled={disabled || loading || !requesterId}
      >
        <option value="">Selecione...</option>
        {otherUsers.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name}
          </option>
        ))}
      </select>

      <label htmlFor="trade-offered">Figurinha oferecida</label>
      <input
        id="trade-offered"
        type="number"
        min={1}
        max={700}
        value={offered}
        onChange={(e) => setOffered(e.target.value)}
        disabled={disabled || loading}
      />

      <label htmlFor="trade-requested">Figurinha desejada</label>
      <input
        id="trade-requested"
        type="number"
        min={1}
        max={700}
        value={requested}
        onChange={(e) => setRequested(e.target.value)}
        disabled={disabled || loading}
      />

      <button
        type="submit"
        disabled={disabled || loading || !requesterId || !targetId || !offered || !requested}
      >
        {loading ? 'Criando...' : 'Criar proposta'}
      </button>
    </form>
  );
}
