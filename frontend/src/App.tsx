import { useState } from 'react';
import type { InitialTradeForm, TradeSuggestion, User } from './api/apiClient';
import { CollectionPage } from './pages/CollectionPage';
import { DuplicatesPage } from './pages/DuplicatesPage';
import { RankingPage } from './pages/RankingPage';
import { SuggestionsPage } from './pages/SuggestionsPage';
import { TradesPage } from './pages/TradesPage';
import { UsersPage } from './pages/UsersPage';
import './App.css';

type Tab = 'users' | 'collection' | 'duplicates' | 'trades' | 'suggestions' | 'ranking';

export default function App() {
  const [tab, setTab] = useState<Tab>('users');
  const [users, setUsers] = useState<User[]>([]);
  const [activeUserId, setActiveUserId] = useState<string | null>(null);
  const [initialTradeForm, setInitialTradeForm] = useState<InitialTradeForm | null>(null);

  const activeUser = users.find((u) => u.id === activeUserId) ?? null;

  function handleUserCreated(user: User) {
    setUsers((prev) => [...prev, user]);
    setActiveUserId(user.id);
  }

  function handleCreateProposalFromSuggestion(suggestion: TradeSuggestion) {
    setInitialTradeForm({
      requesterUserId: suggestion.requesterUserId,
      targetUserId: suggestion.partnerUserId,
      offeredStickerNumber: suggestion.offeredStickerNumber,
      requestedStickerNumber: suggestion.requestedStickerNumber,
    });
    setTab('trades');
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>AlbumX</h1>
        <p>Troca de Figurinhas da Copa — Evolução 2</p>
        {activeUser && (
          <p className="active-user">
            Usuário ativo: <strong>{activeUser.name}</strong>
          </p>
        )}
      </header>

      <nav className="app-nav">
        <button type="button" className={tab === 'users' ? 'active' : ''} onClick={() => setTab('users')}>
          Usuários
        </button>
        <button type="button" className={tab === 'collection' ? 'active' : ''} onClick={() => setTab('collection')}>
          Coleção
        </button>
        <button type="button" className={tab === 'duplicates' ? 'active' : ''} onClick={() => setTab('duplicates')}>
          Repetidas
        </button>
        <button type="button" className={tab === 'trades' ? 'active' : ''} onClick={() => setTab('trades')}>
          Trocas
        </button>
        <button type="button" className={tab === 'suggestions' ? 'active' : ''} onClick={() => setTab('suggestions')}>
          Sugestões
        </button>
        <button type="button" className={tab === 'ranking' ? 'active' : ''} onClick={() => setTab('ranking')}>
          Ranking
        </button>
      </nav>

      <main className="app-main">
        {tab === 'users' && (
          <UsersPage
            users={users}
            activeUserId={activeUserId}
            onUserCreated={handleUserCreated}
            onSelectUser={setActiveUserId}
          />
        )}
        {tab === 'collection' && (
          <CollectionPage activeUserId={activeUserId} activeUserName={activeUser?.name ?? null} />
        )}
        {tab === 'duplicates' && (
          <DuplicatesPage activeUserId={activeUserId} activeUserName={activeUser?.name ?? null} />
        )}
        {tab === 'trades' && (
          <TradesPage users={users} activeUserId={activeUserId} initialTradeForm={initialTradeForm} />
        )}
        {tab === 'suggestions' && (
          <SuggestionsPage
            users={users}
            activeUserId={activeUserId}
            onCreateProposalFromSuggestion={handleCreateProposalFromSuggestion}
          />
        )}
        {tab === 'ranking' && <RankingPage />}
      </main>

      <footer className="app-footer">
        <p>AlbumX — cadastro, coleção, repetidas, trocas, sugestões e ranking</p>
      </footer>
    </div>
  );
}
