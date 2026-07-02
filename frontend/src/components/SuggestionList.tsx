import type { TradeSuggestion, User } from '../api/apiClient';

interface SuggestionListProps {
  suggestions: TradeSuggestion[];
  users: User[];
  onCreateProposal?: (suggestion: TradeSuggestion) => void;
}

function userName(users: User[], id: string): string {
  return users.find((u) => u.id === id)?.name ?? id.slice(0, 8) + '...';
}

export function SuggestionList({ suggestions, users, onCreateProposal }: SuggestionListProps) {
  if (suggestions.length === 0) {
    return <p className="empty-state">Nenhuma sugestão de troca encontrada para este usuário.</p>;
  }

  return (
    <table className="data-table">
      <thead>
        <tr>
          <th>Parceiro</th>
          <th>Você oferece</th>
          <th>Você recebe</th>
          <th>Motivo</th>
          {onCreateProposal && <th>Ação</th>}
        </tr>
      </thead>
      <tbody>
        {suggestions.map((suggestion, index) => (
          <tr key={`${suggestion.partnerUserId}-${suggestion.offeredStickerNumber}-${suggestion.requestedStickerNumber}-${index}`}>
            <td>{userName(users, suggestion.partnerUserId)}</td>
            <td>#{suggestion.offeredStickerNumber}</td>
            <td>#{suggestion.requestedStickerNumber}</td>
            <td>{suggestion.reason}</td>
            {onCreateProposal && (
              <td>
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => onCreateProposal(suggestion)}
                >
                  Criar proposta
                </button>
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
