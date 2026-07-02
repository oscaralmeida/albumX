export interface User {
  id: string;
  name: string;
}

export interface CollectionEntry {
  stickerNumber: number;
  quantity: number;
}

export interface Collection {
  userId: string;
  entries: CollectionEntry[];
}

export interface TradeProposal {
  id: string;
  requesterUserId: string;
  targetUserId: string;
  offeredStickerNumber: number;
  requestedStickerNumber: number;
  status: string;
  createdAt: string;
}

export interface TradeSuggestion {
  requesterUserId: string;
  partnerUserId: string;
  offeredStickerNumber: number;
  requestedStickerNumber: number;
  reason: string;
}

export interface UserRanking {
  position: number;
  userId: string;
  userName: string;
  uniqueStickersCount: number;
  albumCompletionPercentage: number;
  acceptedTradesCount: number;
}

export interface InitialTradeForm {
  requesterUserId: string;
  targetUserId: string;
  offeredStickerNumber: number;
  requestedStickerNumber: number;
}

export interface ApiError {
  code: string;
  message: string;
}

const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options?.headers },
    ...options,
  });

  if (!response.ok) {
    let error: ApiError;
    try {
      error = await response.json();
    } catch {
      error = { code: 'UNKNOWN', message: 'Erro inesperado ao comunicar com a API.' };
    }
    throw error;
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const api = {
  createUser(name: string): Promise<User> {
    return request<User>('/api/users', {
      method: 'POST',
      body: JSON.stringify({ name }),
    });
  },

  getUser(id: string): Promise<User> {
    return request<User>(`/api/users/${id}`);
  },

  addSticker(userId: string, stickerNumber: number): Promise<CollectionEntry> {
    return request<CollectionEntry>(`/api/users/${userId}/collection/stickers`, {
      method: 'POST',
      body: JSON.stringify({ stickerNumber }),
    });
  },

  getCollection(userId: string): Promise<Collection> {
    return request<Collection>(`/api/users/${userId}/collection`);
  },

  getDuplicates(userId: string): Promise<CollectionEntry[]> {
    return request<CollectionEntry[]>(`/api/users/${userId}/collection/duplicates`);
  },

  createTrade(
    requesterUserId: string,
    targetUserId: string,
    offeredStickerNumber: number,
    requestedStickerNumber: number
  ): Promise<TradeProposal> {
    return request<TradeProposal>('/api/trades', {
      method: 'POST',
      body: JSON.stringify({
        requesterUserId,
        targetUserId,
        offeredStickerNumber,
        requestedStickerNumber,
      }),
    });
  },

  listTrades(requesterUserId?: string, targetUserId?: string): Promise<TradeProposal[]> {
    const params = new URLSearchParams();
    if (requesterUserId) params.set('requesterUserId', requesterUserId);
    if (targetUserId) params.set('targetUserId', targetUserId);
    const query = params.toString();
    return request<TradeProposal[]>(`/api/trades${query ? `?${query}` : ''}`);
  },

  getTrade(tradeId: string): Promise<TradeProposal> {
    return request<TradeProposal>(`/api/trades/${tradeId}`);
  },

  acceptTrade(tradeId: string, targetUserId: string): Promise<TradeProposal> {
    return request<TradeProposal>(`/api/trades/${tradeId}/accept`, {
      method: 'POST',
      body: JSON.stringify({ targetUserId }),
    });
  },

  rejectTrade(tradeId: string, targetUserId: string): Promise<TradeProposal> {
    return request<TradeProposal>(`/api/trades/${tradeId}/reject`, {
      method: 'POST',
      body: JSON.stringify({ targetUserId }),
    });
  },

  getTradeSuggestions(userId: string): Promise<TradeSuggestion[]> {
    return request<TradeSuggestion[]>(`/api/users/${userId}/trade-suggestions`);
  },

  getAllTradeSuggestions(): Promise<TradeSuggestion[]> {
    return request<TradeSuggestion[]>('/api/trade-suggestions');
  },

  getRanking(): Promise<UserRanking[]> {
    return request<UserRanking[]>('/api/ranking');
  },
};
