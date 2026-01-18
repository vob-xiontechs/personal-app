from flask import Flask, render_template, jsonify
from dotenv import load_dotenv
import os

# Load environment variables
load_dotenv()

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/api')
def api_home():
    return {'message': 'Welcome to Backend API Third', 'status': 'running', 'service': 'backend-api-third'}

@app.route('/health')
def health():
    return jsonify({'status': 'healthy', 'service': 'backend-api-third'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
