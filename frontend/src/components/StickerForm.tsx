import { FormEvent, useState } from 'react';

interface StickerFormProps {
  onSubmit: (stickerNumber: number) => Promise<void>;
  disabled?: boolean;
}

export function StickerForm({ onSubmit, disabled }: StickerFormProps) {
  const [stickerNumber, setStickerNumber] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const num = parseInt(stickerNumber, 10);
    if (isNaN(num) || num < 1) return;
    setLoading(true);
    try {
      await onSubmit(num);
      setStickerNumber('');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <h3>Adicionar figurinha</h3>
      <label htmlFor="sticker-number">Número da figurinha (1–700)</label>
      <input
        id="sticker-number"
        type="number"
        min={1}
        max={700}
        value={stickerNumber}
        onChange={(e) => setStickerNumber(e.target.value)}
        placeholder="Ex.: 42"
        disabled={disabled || loading}
      />
      <button type="submit" disabled={disabled || loading || !stickerNumber}>
        {loading ? 'Adicionando...' : 'Adicionar'}
      </button>
    </form>
  );
}
