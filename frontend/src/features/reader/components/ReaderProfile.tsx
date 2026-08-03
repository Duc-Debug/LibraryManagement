'use client';

import { useState } from 'react';
import { AlertTriangle, CheckCircle, Clock, BookOpen, Zap, Edit2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import type { ReaderBorrow, ReaderProfile } from '../types/reader.types';
import type { UserAccount } from '@/features/accounts';
import { EditProfileModal } from './EditProfileModal';

interface ReaderProfilePageProps {
  user: UserAccount;
  profile: ReaderProfile;
  borrows: ReaderBorrow[];
  onExtendBorrow?: (borrowId: string) => void;
  onUpdateUser?: (updatedUser: Partial<UserAccount>) => void;
}

export function ReaderProfilePage({
  user,
  profile,
  borrows,
  onExtendBorrow,
  onUpdateUser,
}: ReaderProfilePageProps) {
  const [showEditModal, setShowEditModal] = useState(false);
  const [currentUser, setCurrentUser] = useState(user);
  const activeBorrows = borrows.filter((b) => b.status === 'active');
  const overdueBorrows = borrows.filter((b) => b.status === 'overdue');
  const returnedBorrows = borrows.filter((b) => b.status === 'returned');

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'overdue':
        return 'text-destructive bg-destructive/10 border-destructive/30';
      case 'active':
        return 'text-accent bg-accent/10 border-accent/30';
      default:
        return 'text-foreground/60 bg-muted border-border';
    }
  };

  const getDaysUntilDueColor = (daysUntilDue: number | undefined) => {
    if (!daysUntilDue) return 'text-foreground/60';
    if (daysUntilDue < 0) return 'text-destructive font-semibold';
    if (daysUntilDue <= 3) return 'text-secondary font-semibold';
    return 'text-accent';
  };

  const handleSaveProfile = (updatedUser: Partial<UserAccount>) => {
    const newUser = { ...currentUser, ...updatedUser };
    setCurrentUser(newUser);
    onUpdateUser?.(updatedUser);
  };

  return (
    <div className="w-full min-h-screen bg-gradient-to-b from-background to-muted/30 p-6">
      <div className="max-w-5xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground mb-1">
            Hồ sơ đọc giả
          </h1>
          <p className="text-foreground/60">Thông tin cá nhân</p>
        </div>

        {/* User Info Card */}
        <div className="bg-card border border-border rounded-lg p-6 mb-8">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-2xl font-bold text-foreground">{currentUser.fullName}</h2>
              <p className="text-foreground/60">@{currentUser.username}</p>
            </div>
            <div className="flex flex-col items-end gap-4">
              <div className="text-right">
                <div className="text-3xl font-bold text-primary">
                  {profile.currentBorrows}/{profile.borrowLimit}
                </div>
                <p className="text-sm text-foreground/60">Sách đang mượn</p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowEditModal(true)}
                className="gap-2"
              >
                <Edit2 className="w-4 h-4" />
                Chỉnh sửa
              </Button>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          {/* Active Borrows */}
          <div className="bg-card border border-accent/30 rounded-lg p-4">
            <div className="flex items-center gap-3 mb-2">
              <div className="bg-accent/10 p-2 rounded-lg">
                <BookOpen className="w-5 h-5 text-accent" />
              </div>
              <span className="text-sm text-foreground/60">Đang mượn</span>
            </div>
            <p className="text-3xl font-bold text-foreground">
              {activeBorrows.length}
            </p>
          </div>

          {/* Overdue */}
          <div className="bg-card border border-destructive/30 rounded-lg p-4">
            <div className="flex items-center gap-3 mb-2">
              <div className="bg-destructive/10 p-2 rounded-lg">
                <AlertTriangle className="w-5 h-5 text-destructive" />
              </div>
              <span className="text-sm text-foreground/60">Quá hạn</span>
            </div>
            <p className="text-3xl font-bold text-destructive">
              {overdueBorrows.length}
            </p>
          </div>

          {/* Returned */}
          <div className="bg-card border border-accent/30 rounded-lg p-4">
            <div className="flex items-center gap-3 mb-2">
              <div className="bg-accent/10 p-2 rounded-lg">
                <CheckCircle className="w-5 h-5 text-accent" />
              </div>
              <span className="text-sm text-foreground/60">Đã trả</span>
            </div>
            <p className="text-3xl font-bold text-foreground">
              {returnedBorrows.length}
            </p>
          </div>
        </div>

        {/* Borrowing Limits */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Borrow Limit */}
          <div className="bg-card border border-border rounded-lg p-6">
            <h3 className="text-lg font-bold text-foreground mb-4 flex items-center gap-2">
              <Zap className="w-5 h-5 text-primary" />
              Giới hạn mượn sách
            </h3>
            <div className="mb-4">
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm text-foreground/60">Tiến độ</span>
                <span className="text-sm font-semibold text-foreground">
                  {profile.currentBorrows}/{profile.borrowLimit}
                </span>
              </div>
              <div className="w-full bg-muted rounded-full h-2 overflow-hidden">
                <div
                  className="h-full bg-primary transition-all"
                  style={{
                    width: `${(profile.currentBorrows / profile.borrowLimit) * 100}%`,
                  }}
                />
              </div>
            </div>
            <p className="text-sm text-foreground/60">
              Bạn có thể mượn thêm{' '}
              <span className="font-semibold text-foreground">
                {profile.borrowLimit - profile.currentBorrows}
              </span>{' '}
              cuốn sách
            </p>
          </div>

          {/* Reservation Limit */}
          <div className="bg-card border border-border rounded-lg p-6">
            <h3 className="text-lg font-bold text-foreground mb-4 flex items-center gap-2">
              <Clock className="w-5 h-5 text-primary" />
              Giới hạn đặt chỗ
            </h3>
            <div className="mb-4">
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm text-foreground/60">Tiến độ</span>
                <span className="text-sm font-semibold text-foreground">
                  {profile.currentReservations}/{profile.reservationLimit}
                </span>
              </div>
              <div className="w-full bg-muted rounded-full h-2 overflow-hidden">
                <div
                  className="h-full bg-secondary transition-all"
                  style={{
                    width: `${(profile.currentReservations / profile.reservationLimit) * 100}%`,
                  }}
                />
              </div>
            </div>
            <p className="text-sm text-foreground/60">
              Bạn có thể đặt chỗ thêm{' '}
              <span className="font-semibold text-foreground">
                {profile.reservationLimit - profile.currentReservations}
              </span>{' '}
              cuốn sách
            </p>
          </div>
        </div>

        {/* Borrowed Books */}
        <div className="bg-card border border-border rounded-lg overflow-hidden">
          <div className="p-6 border-b border-border bg-muted/50">
            <h3 className="text-lg font-bold text-foreground">Lịch sử mượn sách</h3>
          </div>

          <div className="divide-y divide-border">
            {borrows.length > 0 ? (
              borrows.map((borrow) => (
                <div
                  key={borrow.id}
                  className={`p-4 border-l-4 ${getStatusColor(
                    borrow.status
                  )}`}
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      <h4 className="font-semibold text-foreground mb-1">
                        {borrow.bookTitle}
                      </h4>
                      <p className="text-sm text-foreground/60 mb-3">
                        của {borrow.bookAuthor}
                      </p>

                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                        <div>
                          <p className="text-xs text-foreground/50 uppercase font-semibold mb-0.5">
                            Mượn từ
                          </p>
                          <p className="text-foreground">{borrow.borrowDate}</p>
                        </div>
                        <div>
                          <p className="text-xs text-foreground/50 uppercase font-semibold mb-0.5">
                            Hạn trả
                          </p>
                          <p className={getDaysUntilDueColor(
                            borrow.daysUntilDue
                          )}>
                            {borrow.dueDate}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-foreground/50 uppercase font-semibold mb-0.5">
                            Trạng thái
                          </p>
                          <div className="inline-block">
                            {borrow.status === 'overdue' ? (
                              <div className="bg-destructive/20 text-destructive px-3 py-1 rounded-full text-xs font-medium">
                                Quá hạn
                              </div>
                            ) : borrow.status === 'active' ? (
                              <div className="bg-accent/20 text-accent px-3 py-1 rounded-full text-xs font-medium">
                                Đang mượn
                              </div>
                            ) : (
                              <div className="bg-foreground/10 text-foreground px-3 py-1 rounded-full text-xs font-medium">
                                Đã trả
                              </div>
                            )}
                          </div>
                        </div>
                        {borrow.daysUntilDue !== undefined && (
                          <div>
                            <p className="text-xs text-foreground/50 uppercase font-semibold mb-0.5">
                              Còn lại
                            </p>
                            <p className={getDaysUntilDueColor(
                              borrow.daysUntilDue
                            )}>
                              {borrow.daysUntilDue > 0
                                ? `${borrow.daysUntilDue} ngày`
                                : `${Math.abs(borrow.daysUntilDue)} ngày`}
                            </p>
                          </div>
                        )}
                      </div>
                    </div>

                    {borrow.status === 'active' && borrow.daysUntilDue! <= 5 && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => onExtendBorrow?.(borrow.id)}
                      >
                        Gia hạn
                      </Button>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <div className="p-8 text-center">
                <BookOpen className="w-12 h-12 text-foreground/20 mx-auto mb-4" />
                <p className="text-foreground/60">Bạn chưa mượn sách nào</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      {showEditModal && (
        <EditProfileModal
          user={currentUser}
          onClose={() => setShowEditModal(false)}
          onSave={handleSaveProfile}
        />
      )}
    </div>
  );
}
