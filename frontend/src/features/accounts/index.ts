// PUBLIC API: chỉ export những gì được phép dùng bên ngoài feature "accounts"

export { default as AccountsPage } from './components/AccountsPage';

export { getAccounts, mockUserAccounts } from './api/getAccounts';

export type { UserAccount, UserRole } from './types/account.types';
