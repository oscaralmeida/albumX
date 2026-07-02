import type { CollectionEntry } from '../api/apiClient';

interface CollectionListProps {
  entries: CollectionEntry[];
  emptyMessage?: string;
}

export function CollectionList({ entries, emptyMessage = 'Nenhuma figurinha na coleção.' }: CollectionListProps) {
  if (entries.length === 0) {
    return <p className="empty-state">{emptyMessage}</p>;
  }

  return (
    <table className="data-table">
      <thead>
        <tr>
          <th>Figurinha</th>
          <th>Quantidade</th>
        </tr>
      </thead>
      <tbody>
        {entries.map((entry) => (
          <tr key={entry.stickerNumber}>
            <td>#{entry.stickerNumber}</td>
            <td>{entry.quantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
