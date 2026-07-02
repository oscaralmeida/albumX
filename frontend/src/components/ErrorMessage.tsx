import type { ApiError } from '../api/apiClient';

interface ErrorMessageProps {
  error: ApiError | null;
  onDismiss?: () => void;
}

export function ErrorMessage({ error, onDismiss }: ErrorMessageProps) {
  if (!error) return null;

  return (
    <div className="error-banner" role="alert">
      <span>{error.message}</span>
      {onDismiss && (
        <button type="button" className="btn-link" onClick={onDismiss}>
          Fechar
        </button>
      )}
    </div>
  );
}
