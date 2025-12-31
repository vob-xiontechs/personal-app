import React from 'react';
import { isUsingGraphQL, switchToGraphQL, switchToAxios, toggleApiMode } from '../../infrastructure/config/api';

interface ApiSwitcherProps {
  className?: string;
}

export const ApiSwitcher: React.FC<ApiSwitcherProps> = ({ className = '' }) => {
  const currentMode = isUsingGraphQL() ? 'GraphQL' : 'Axios';

  return (
    <div className={`api-switcher ${className}`} style={{
      position: 'fixed',
      top: '20px',
      right: '20px',
      zIndex: 1000,
      background: 'rgba(255, 255, 255, 0.95)',
      backdropFilter: 'blur(10px)',
      border: '1px solid rgba(0, 0, 0, 0.1)',
      borderRadius: '8px',
      padding: '12px',
      boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
      fontSize: '14px',
      fontFamily: 'Inter, sans-serif'
    }}>
      <div style={{ marginBottom: '8px', fontWeight: '600', color: '#374151' }}>
        API Mode: <span style={{
          color: isUsingGraphQL() ? '#7c3aed' : '#059669',
          fontWeight: '700'
        }}>
          {currentMode}
        </span>
      </div>

      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
        <button
          onClick={switchToGraphQL}
          disabled={isUsingGraphQL()}
          style={{
            padding: '6px 12px',
            border: '1px solid #d1d5db',
            borderRadius: '4px',
            background: isUsingGraphQL() ? '#7c3aed' : '#ffffff',
            color: isUsingGraphQL() ? '#ffffff' : '#374151',
            cursor: isUsingGraphQL() ? 'not-allowed' : 'pointer',
            fontSize: '12px',
            fontWeight: '500',
            transition: 'all 0.2s ease'
          }}
        >
          📡 GraphQL
        </button>

        <button
          onClick={switchToAxios}
          disabled={!isUsingGraphQL()}
          style={{
            padding: '6px 12px',
            border: '1px solid #d1d5db',
            borderRadius: '4px',
            background: !isUsingGraphQL() ? '#059669' : '#ffffff',
            color: !isUsingGraphQL() ? '#ffffff' : '#374151',
            cursor: !isUsingGraphQL() ? 'not-allowed' : 'pointer',
            fontSize: '12px',
            fontWeight: '500',
            transition: 'all 0.2s ease'
          }}
        >
          🔄 Axios
        </button>

        <button
          onClick={toggleApiMode}
          style={{
            padding: '6px 12px',
            border: '1px solid #d1d5db',
            borderRadius: '4px',
            background: '#f3f4f6',
            color: '#374151',
            cursor: 'pointer',
            fontSize: '12px',
            fontWeight: '500',
            transition: 'all 0.2s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.background = '#e5e7eb';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.background = '#f3f4f6';
          }}
        >
          🔄 Toggle
        </button>
      </div>

      <div style={{
        marginTop: '8px',
        fontSize: '11px',
        color: '#6b7280',
        lineHeight: '1.4'
      }}>
        Switch between GraphQL and REST APIs.<br/>
        Auto-fallback enabled on network errors.
      </div>
    </div>
  );
};
