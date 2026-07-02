interface TradeActionsProps {
  tradeId: string;
  disabled?: boolean;
  onAccept: (tradeId: string) => void;
  onReject: (tradeId: string) => void;
}

export function TradeActions({ tradeId, disabled, onAccept, onReject }: TradeActionsProps) {
  return (
    <div className="trade-actions">
      <button
        type="button"
        className="btn-accept"
        disabled={disabled}
        onClick={() => onAccept(tradeId)}
      >
        Aceitar
      </button>
      <button
        type="button"
        className="btn-reject"
        disabled={disabled}
        onClick={() => onReject(tradeId)}
      >
        Recusar
      </button>
    </div>
  );
}
